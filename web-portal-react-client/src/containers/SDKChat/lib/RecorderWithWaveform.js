import React, { useEffect, useRef, useState } from "react";
import WaveSurfer from "wavesurfer.js";
import RecordPlugin from "wavesurfer.js/dist/plugins/record.esm";
import { Mic, Pause, Play, X } from "lucide-react";
import toWav from "audiobuffer-to-wav";

// blob转wav函数
function blobToWav(blob) {
  // 已经是wav格式直接返回
  if (blob.type === "audio/wav") {
    return Promise.resolve(blob);
  }
  // 否则进行转换
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = function () {
      const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      audioCtx
        .decodeAudioData(reader.result)
        .then((buffer) => {
          const wavData = toWav(buffer);
          const wavBlob = new Blob([wavData], { type: "audio/wav" });
          resolve(wavBlob);
        })
        .catch((err) => reject(err));
    };
    reader.onerror = reject;
    reader.readAsArrayBuffer(blob);
  });
}

const RecorderWithWaveform = ({ onAudioReady, onRecordingStatusChange }) => {
  const [isRecording, setIsRecording] = useState(false);
  const [isPaused, setIsPaused] = useState(false);
  const [audioBlob, setAudioBlob] = useState(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [recordTime, setRecordTime] = useState("00:00");
  const [recordTimerId, setRecordTimerId] = useState(null);

  const isRecordingRef = useRef(false);

  const recordingWaveformRef = useRef(null);
  const playbackWaveformRef = useRef(null);
  const recordingWaveSurferRef = useRef(null);
  const playbackWaveSurferRef = useRef(null);
  const recordPluginRef = useRef(null);

  useEffect(() => {
    if (recordingWaveformRef.current && !recordingWaveSurferRef.current) {
      recordingWaveSurferRef.current = WaveSurfer.create({
        container: recordingWaveformRef.current,
        waveColor: "#4fc3f7",
        progressColor: "#1976d2",
        height: 50,
        barWidth: 2,
        cursorWidth: 2,
        responsive: true,
      });

      recordPluginRef.current = recordingWaveSurferRef.current.registerPlugin(
        RecordPlugin.create({
          renderRecordedAudio: false,
          scrollingWaveform: false,
          continuousWaveform: true,
          continuousWaveformDuration: 30,
        }),
      );

      recordPluginRef.current.on("record-end", async (blob) => {
        try {
          const wavBlob = await blobToWav(blob);
          setAudioBlob(wavBlob);
          recordingWaveSurferRef.current.empty();
          const audioUrl = URL.createObjectURL(wavBlob);
          recordingWaveSurferRef.current.load(audioUrl);
        } catch (err) {
          console.error("Audio conversion to WAV failed.", err);
        }
      });

      recordPluginRef.current.on("record-progress", (time) => {
        updateProgress(time);
      });
    }

    return () => {
      if (recordingWaveSurferRef.current) {
        recordingWaveSurferRef.current.destroy();
        recordingWaveSurferRef.current = null;
      }
    };
  }, []);

  useEffect(() => {
    if (audioBlob && playbackWaveformRef.current) {
      if (playbackWaveSurferRef.current) {
        playbackWaveSurferRef.current.destroy();
      }
      playbackWaveSurferRef.current = WaveSurfer.create({
        container: playbackWaveformRef.current,
        waveColor: "#4fc3f7",
        progressColor: "#1976d2",
        height: 50,
        barWidth: 2,
        cursorWidth: 2,
        responsive: true,
      });
      playbackWaveSurferRef.current.on("finish", () => setIsPlaying(false));
      const audioUrl = URL.createObjectURL(audioBlob);
      playbackWaveSurferRef.current.load(audioUrl);
    }
    return () => {
      if (playbackWaveSurferRef.current) {
        playbackWaveSurferRef.current.destroy();
        playbackWaveSurferRef.current = null;
      }
    };
  }, [audioBlob]);

  useEffect(() => {
    if (audioBlob && onAudioReady) {
      onAudioReady(audioBlob);
    }
  }, [audioBlob, onAudioReady]);

  const updateProgress = (time) => {
    const minutes = Math.floor((time % 3600000) / 60000);
    const seconds = Math.floor((time % 60000) / 1000);
    setRecordTime(`${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`);
  };

  const startRecording = async () => {
    try {
      setIsRecording(true);
      isRecordingRef.current = true;
      setIsPaused(false);
      setAudioBlob(null);
      setRecordTime("00:00");

      if (onRecordingStatusChange) onRecordingStatusChange(true);

      const deviceId = undefined;
      await recordPluginRef.current.startRecording({ deviceId });

      const timerId = setTimeout(() => {
        if (isRecordingRef.current) {
          stopRecording();
        }
      }, 30 * 1000);
      setRecordTimerId(timerId);
    } catch (error) {
      setIsRecording(false);
      isRecordingRef.current = false;
      if (onRecordingStatusChange) onRecordingStatusChange(false);
      console.error("Error starting recording:", error);
    }
  };

  const stopRecording = () => {
    if (recordTimerId) {
      clearTimeout(recordTimerId);
      setRecordTimerId(null);
    }
    if (recordPluginRef.current && isRecordingRef.current) {
      recordPluginRef.current.stopRecording();
      setIsRecording(false);
      isRecordingRef.current = false;
      setIsPaused(false);

      if (onRecordingStatusChange) onRecordingStatusChange(false);
    }
  };

  const pauseRecording = () => {
    if (recordPluginRef.current) {
      recordPluginRef.current.pauseRecording();
      setIsPaused(true);
    }
  };

  const resumeRecording = () => {
    if (recordPluginRef.current) {
      recordPluginRef.current.resumeRecording();
      setIsPaused(false);
    }
  };

  const togglePlayback = () => {
    if (playbackWaveSurferRef.current) {
      playbackWaveSurferRef.current.playPause();
      setIsPlaying(!isPlaying);
    }
  };

  const cancelRecording = () => {
    stopRecording();
    resetRecorder();
  };

  const resetRecorder = () => {
    setAudioBlob(null);
    setIsPlaying(false);
    setIsRecording(false);
    isRecordingRef.current = false;
    setIsPaused(false);
    setRecordTime("00:00");
    if (onRecordingStatusChange) onRecordingStatusChange(false);
    if (recordTimerId) {
      clearTimeout(recordTimerId);
      setRecordTimerId(null);
    }
    if (playbackWaveSurferRef.current) {
      playbackWaveSurferRef.current.empty();
    }
    if (recordingWaveSurferRef.current) {
      recordingWaveSurferRef.current.empty();
    }
  };

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        gap: "8px",
        width: "100%",
        padding: window.innerWidth <= 600 ? "8px" : "17px",
        backgroundColor: "#f5f5f5",
      }}
    >
      {/* 录音/停止/暂停按钮 */}
      {!isRecording && !audioBlob ? (
        <button
          onClick={startRecording}
          style={{
            background: "none",
            border: "none",
            cursor: "pointer",
            padding: "4px",
          }}
          title="Start recording"
        >
          <Mic color="#0064ad" size={20} />
        </button>
      ) : isRecording ? (
        <>
          <button
            onClick={stopRecording}
            style={{
              background: "none",
              border: "none",
              cursor: "pointer",
              padding: "4px",
            }}
            title="Stop recording"
          >
            <div
              style={{
                width: "16px",
                height: "16px",
                borderRadius: "50%",
                backgroundColor: "#d32f2f",
              }}
            />
          </button>
          <button
            onClick={isPaused ? resumeRecording : pauseRecording}
            style={{
              background: "none",
              border: "none",
              cursor: "pointer",
              padding: "4px",
            }}
            title={isPaused ? "Restore recording" : "Pause recording"}
          >
            {isPaused ? <Play size={20} /> : <Pause size={20} />}
          </button>
        </>
      ) : null}

      {/* 波形图 */}
      <div style={{ flex: 1, position: "relative", height: "50px" }}>
        <div
          ref={recordingWaveformRef}
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "50px",
            backgroundColor: "#eee",
            borderRadius: "4px",
            opacity: isRecording ? 1 : 0,
            visibility: isRecording ? "visible" : "hidden",
            transition: "opacity 0.2s ease-in-out",
          }}
        />
        <div
          ref={playbackWaveformRef}
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "50px",
            minWidth: "100px",
            backgroundColor: "#eee",
            borderRadius: "4px",
            opacity: audioBlob && !isRecording ? 1 : 0,
            visibility: audioBlob && !isRecording ? "visible" : "hidden",
            transition: "opacity 0.2s ease-in-out",
          }}
        />
        {!isRecording && !audioBlob && (
          <div
            style={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "50px",
              backgroundColor: "#eee",
              borderRadius: "4px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "#999",
              fontSize: "12px",
            }}
          >
            Click mic to start recording
          </div>
        )}
      </div>

      {/* 录音时间 */}
      <div
        style={{
          minWidth: "40px",
          textAlign: "center",
          fontFamily: "monospace",
          fontSize: "14px",
        }}
      >
        {recordTime}
      </div>

      {/* 仅 播放/取消 按钮（无发送按钮） */}
      {audioBlob && !isRecording && (
        <>
          <button
            onClick={togglePlayback}
            style={{
              background: "none",
              border: "none",
              cursor: "pointer",
              padding: "4px",
            }}
            title={isPlaying ? "Pause" : "Play"}
          >
            {isPlaying ? <Pause size={20} /> : <Play size={20} />}
          </button>
          <button
            onClick={cancelRecording}
            style={{
              background: "none",
              border: "none",
              cursor: "pointer",
              padding: "4px",
            }}
            title="Cancel"
          >
            <X size={20} color="#d32f2f" />
          </button>
        </>
      )}
    </div>
  );
};

export default RecorderWithWaveform;
