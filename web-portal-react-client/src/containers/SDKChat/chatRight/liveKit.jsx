import { GridLayout, ParticipantTile, RoomAudioRenderer, RoomContext, useTracks } from "@livekit/components-react";
import "@livekit/components-styles";
import { Room, Track } from "livekit-client";
import { Mic, MicOff, Phone, Video, VideoOff } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { useChatManagerContext } from "../context/ChatManagerContext";
import "./audioCallWindow.scss";
import AudioDraggableWindow from "./AudioDraggableWindow";
import DraggableVideoWindow from "./draggableVideoWindow";
import "./livekit.scss";

export default function LiveKit() {
  const { currentChannel, callToken, callType, callChannelId, callCancel, callLeave, isCaller, callInvite } =
    useChatManagerContext();

  const serverUrl = process.env.REACT_APP_SDK_VIDEO_SOCKET;
  const token = callToken;
  const leaveType = "video_leave";

  console.log(currentChannel, "currentChannel");

  // room实例只创建一次
  const [room] = useState(
    () =>
      new Room({
        adaptiveStream: true,
        dynacast: true,
        videoCaptureDefaults: {
          resolution: { width: 1280, height: 720 },
        },
      }),
  );

  const [micEnabled, setMicEnabled] = useState(true);
  const [camEnabled, setCamEnabled] = useState(true);
  const [remoteJoined, setRemoteJoined] = useState(false);
  const [destroying, setDestroying] = useState(false);

  const remoteJoinedRef = useRef(remoteJoined);

  // === 通话开始/结束时长计算 ===
  const callStartTimeRef = useRef(null);

  // 通话时长
  const [callDuration, setCallDuration] = useState("00:00:00");

  // 保证 timer 回调总是拿到最新 remoteJoined
  useEffect(() => {
    remoteJoinedRef.current = remoteJoined;
  }, [remoteJoined]);

  // refs，避免闭包
  const callTypeRef = useRef(callType);
  const callChannelIdRef = useRef(callChannelId);
  useEffect(() => {
    callTypeRef.current = callType;
    callChannelIdRef.current = callChannelId;
  }, [callType, callChannelId]);

  // 挂断
  const hangup = async () => {
    setDestroying(true);
    await room.disconnect();

    if (!remoteJoinedRef.current) {
      // 对方未接通，拨打方主动挂断走 callCancel
      callCancel({
        channel_id: callChannelIdRef.current,
        type: callTypeRef.current,
      });
      return;
    }

    let call_time = "";
    if (callStartTimeRef.current) {
      const seconds = Math.round((Date.now() - callStartTimeRef.current) / 1000);
      const pad = (n) => n.toString().padStart(2, "0");
      const h = pad(Math.floor(seconds / 3600));
      const m = pad(Math.floor((seconds % 3600) / 60));
      const s = pad(seconds % 60);
      call_time = `${h}:${m}:${s}`;
    }
    const params = {
      type: leaveType,
      channel_id: callChannelIdRef.current,
      call_time, // 格式：hh:mm:ss
    };
    callLeave(params);
  };

  // 监听对方加入（只处理非本地participant）
  useEffect(() => {
    const onParticipantConnected = (participant) => {
      if (!participant.isLocal) {
        setRemoteJoined(true);
        // 拨打方在远端真正加入时计时（只设置一次）
        if (isCaller && !callStartTimeRef.current) {
          callStartTimeRef.current = Date.now();
        }
      }
    };
    room.on("participantConnected", onParticipantConnected);
    return () => room.off("participantConnected", onParticipantConnected);
  }, [room, isCaller]);

  // 仅拨打方，等待30s对方加入，否则自动cancel
  useEffect(() => {
    if (!isCaller) return;
    if (remoteJoined) return;
    const timer = setTimeout(() => {
      if (!remoteJoinedRef.current) {
        if (callChannelId && callType) {
          callCancel({
            channel_id: callChannelId,
            type: callType,
          });
        }
        room.disconnect();
        setDestroying(true);
      }
    }, 30000);
    return () => clearTimeout(timer);
  }, [isCaller, remoteJoined, callChannelId, callType, callCancel, room]);

  // 同步本地设备状态
  useEffect(() => {
    if (!room) return;

    room.localParticipant.setCameraEnabled(true);
    room.localParticipant.setMicrophoneEnabled(true);

    const updateStatus = () => {
      setMicEnabled(room.localParticipant.isMicrophoneEnabled);
      setCamEnabled(room.localParticipant.isCameraEnabled);
    };

    updateStatus();
    room.localParticipant.on("trackMuted", updateStatus);
    room.localParticipant.on("trackUnmuted", updateStatus);
    room.localParticipant.on("localTrackPublished", updateStatus);
    room.localParticipant.on("localTrackUnpublished", updateStatus);

    return () => {
      room.localParticipant.off("trackMuted", updateStatus);
      room.localParticipant.off("trackUnmuted", updateStatus);
      room.localParticipant.off("localTrackPublished", updateStatus);
      room.localParticipant.off("localTrackUnpublished", updateStatus);
    };
  }, [room]);

  const toggleMic = () => {
    room.localParticipant.setMicrophoneEnabled(!micEnabled);
  };

  const toggleCam = () => {
    room.localParticipant.setCameraEnabled(!camEnabled);
  };

  // 监听 LiveKit room 的 disconnected 事件
  useEffect(() => {
    const handleDisconnected = () => {
      setDestroying(true);
    };
    room.on("disconnected", handleDisconnected);
    return () => room.off("disconnected", handleDisconnected);
  }, [room]);

  // 监听token变为null时，主动断开并销毁
  useEffect(() => {
    if ((!token || destroying) && room && room.connectionState !== "disconnected") {
      room.disconnect();
      setDestroying(true);
    }
  }, [token, destroying, room]);

  // 建立连接
  useEffect(() => {
    if (!token || !serverUrl) return;
    let mounted = true;

    const connect = async () => {
      if (mounted) {
        try {
          await room.connect(serverUrl, token);
        } catch (e) {
          console.error("LiveKit room connection failed:", e);
        }
      }
    };
    connect();

    return () => {
      mounted = false;
      // 避免重复断开
      if (room && room.connectionState !== "disconnected") {
        room.disconnect();
      }
    };
  }, [room, token, serverUrl]);

  // 被叫方：在 callInvite（即 video_join）生效时才开启计时
  useEffect(() => {
    if (!isCaller && callInvite && !callStartTimeRef.current) {
      callStartTimeRef.current = Date.now();
      setRemoteJoined(true);
    }
  }, [isCaller, callInvite]);

  // 定时更新通话时长
  useEffect(() => {
    if (!callStartTimeRef.current) return;
    const timer = setInterval(() => {
      const seconds = Math.round((Date.now() - callStartTimeRef.current) / 1000);
      const pad = (n) => n.toString().padStart(2, "0");
      const h = pad(Math.floor(seconds / 3600));
      const m = pad(Math.floor((seconds % 3600) / 60));
      const s = pad(seconds % 60);
      setCallDuration(`${h}:${m}:${s}`);
    }, 1000);
    return () => clearInterval(timer);
  }, [callStartTimeRef.current]);

  // 每次 remoteJoined 变为 true 时都设/重置定时器（定时器直接读取 ref，避免 useEffect 依赖 ref 导致失效）
  useEffect(() => {
    if (!callStartTimeRef.current || !remoteJoined) return;
    setCallDuration("00:00:00");
    const timer = setInterval(() => {
      const seconds = Math.max(0, Math.round((Date.now() - callStartTimeRef.current) / 1000));
      const pad = (n) => n.toString().padStart(2, "0");
      const h = pad(Math.floor(seconds / 3600));
      const m = pad(Math.floor((seconds % 3600) / 60));
      const s = pad(seconds % 60);
      setCallDuration(`${h}:${m}:${s}`);
    }, 1000);
    return () => clearInterval(timer);
  }, [remoteJoined]);

  // token/serverUrl/destroying任何一项无效，直接不渲染
  if (!token || !serverUrl || destroying) return null;

  // ---正常渲染内容---
  return (
    <RoomContext.Provider value={room}>
      {callType === 2 ? (
        <DraggableVideoWindow>
          <div data-lk-theme="default" style={{ width: "100%", height: "100%" }} className="draggableVideoWindow">
            {/* 通话时长显示区域 */}
            {remoteJoined && <div className="lk-call-timer">{callDuration}</div>}
            <MyVideoConference />
            <RoomAudioRenderer />
            <div className="control">
              <div
                className="imgBox"
                onClick={toggleMic}
                title={micEnabled ? "Turn off the microphone" : "Turn on the microphone"}
              >
                {micEnabled ? <Mic size={30} /> : <MicOff size={30} />}
              </div>
              <div className="imgBox phone" onClick={hangup} title="Hang up">
                <Phone size={30} className="phone-icon" />
              </div>
              <div
                className="imgBox"
                onClick={toggleCam}
                title={camEnabled ? "Turn off the camera" : "Turn on the camera"}
              >
                {camEnabled ? <Video size={30} /> : <VideoOff size={30} />}
              </div>
            </div>
          </div>
        </DraggableVideoWindow>
      ) : (
        <AudioDraggableWindow>
          <MyAudioConference
            micEnabled={micEnabled}
            toggleMic={toggleMic}
            callCancel={callCancel}
            callLeave={hangup}
            callDuration={callDuration}
            remoteJoined={remoteJoined}
            isCaller={isCaller}
          />
        </AudioDraggableWindow>
      )}
    </RoomContext.Provider>
  );
}

function MyVideoConference() {
  const { isCaller, callInvite } = useChatManagerContext();
  // 优先展示远端视频流，远端接通后显示右上角本地流小窗
  const cameraTracks = useTracks([{ source: Track.Source.Camera, withPlaceholder: false }], { onlySubscribed: true });
  const localTrack = cameraTracks.find((trackRef) => trackRef.participant.isLocal);
  const remoteTrack = cameraTracks.find((trackRef) => !trackRef.participant.isLocal);

  let mainTrackToDisplay = null;
  if (remoteTrack) {
    mainTrackToDisplay = remoteTrack;
  } else if (localTrack) {
    mainTrackToDisplay = localTrack;
  }

  // 防御性检查：track必须有效并属于摄像头流集合，避免渲染无效track导致报错
  if (
    !mainTrackToDisplay ||
    !cameraTracks.includes(mainTrackToDisplay) ||
    (remoteTrack && !cameraTracks.includes(remoteTrack)) ||
    (localTrack && !cameraTracks.includes(localTrack))
  ) {
    // 根据状态显示不同的等待界面
    const getWaitingInfo = () => {
      if (isCaller) {
        return {
          title: "Calling...",
          description: "Waiting for the other party to answer",
          icon: "📞",
        };
      } else if (callInvite) {
        return {
          title: "Joining video call...",
          description: "Connecting to the video session",
          icon: "📹",
        };
      } else {
        return {
          title: "Preparing video call...",
          description: "Setting up your camera and connection",
          icon: "🎥",
        };
      }
    };

    const waitingInfo = getWaitingInfo();

    return (
      <div className="video-waiting-container">
        <div className="video-waiting-content">
          <div className="waiting-avatar">
            <div className="avatar-placeholder">
              <div className="pulse-ring"></div>
              <div className="avatar-icon">{waitingInfo.icon}</div>
            </div>
          </div>
          <div className="waiting-text">
            <h3>{waitingInfo.title}</h3>
            <p>{waitingInfo.description}</p>
          </div>
          <div className="connection-status">
            <div className="status-dots">
              <span className="dot"></span>
              <span className="dot"></span>
              <span className="dot"></span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={{ position: "relative", width: "100%", height: "100%" }}>
      {/* 主窗口显示 */}
      <GridLayout tracks={[mainTrackToDisplay]}>
        <ParticipantTile trackRef={mainTrackToDisplay} />
      </GridLayout>
      {/* 远端接通后右上角显示本地流小窗 */}
      {remoteTrack && localTrack && (
        <div
          style={{
            position: "absolute",
            top: 20,
            right: 20,
            width: 160,
            height: 90,
            zIndex: 2,
            borderRadius: 8,
            overflow: "hidden",
            boxShadow: "0 2px 8px rgba(0,0,0,0.15)",
            background: "#222",
          }}
        >
          <ParticipantTile trackRef={localTrack} />
        </div>
      )}
    </div>
  );
}

// 新增的音频通话组件
function MyAudioConference({ micEnabled, toggleMic, callCancel, callLeave, callDuration, remoteJoined, isCaller }) {
  const { currentChannel, callType, callChannelId } = useChatManagerContext();

  // 使用 currentChannel.channel_avatar 直接作为头像展示（可自定义样式）
  function renderAvatar() {
    // 先尝试 img 链接，否则当作文本
    const avatar = currentChannel?.channel_avatar;
    if (avatar && /^https?:\/\//i.test(avatar.trim())) {
      return <img src={avatar} alt="avatar" style={{ width: "100%", height: "100%", borderRadius: "50%" }} />;
    }
    // 否则直接用文本
    return <span>{avatar ? String(avatar).trim()[0]?.toUpperCase() : "?"}</span>;
  }

  // 统一挂断逻辑，未接通且主动挂断走callCancel（带参数），接通走callLeave
  const handleHangup = () => {
    if (remoteJoined) {
      callLeave();
    } else {
      callCancel({
        channel_id: callChannelId,
        type: callType,
      });
    }
  };

  // 1. 拨打方未接通
  if (isCaller && !remoteJoined) {
    return (
      <div className="audio-call-window">
        <div className="audio-avatar">{renderAvatar()}</div>
        <div className="audio-call-info">
          <div className="audio-call-name">{currentChannel?.channel_display_name || "Unknown"}</div>
          <div className="audio-call-desc">
            <span className="calling-text">Calling...</span>
            <div className="calling-dots">
              <span className="dot"></span>
              <span className="dot"></span>
              <span className="dot"></span>
            </div>
          </div>
        </div>
        <div className="audio-call-ops">
          <button className="audio-btn audio-btn-hangup" onClick={handleHangup}>
            <Phone />
          </button>
        </div>
      </div>
    );
  }

  // 2. 接听方未接通
  if (!isCaller && !remoteJoined) {
    return (
      <div className="audio-call-window">
        <div className="audio-avatar">{renderAvatar()}</div>
        <div className="audio-call-info">
          <div className="audio-call-name">{currentChannel?.channel_display_name || "Unknown"}</div>
          <div className="audio-call-desc">
            <span className="incoming-text">Incoming audio call</span>
            <div className="pulse-indicator">
              <span className="pulse-dot"></span>
            </div>
          </div>
        </div>
        <div className="audio-call-ops">
          <button className="audio-btn audio-btn-hangup" onClick={handleHangup}>
            <Phone />
          </button>

          <button
            className="audio-btn audio-btn-accept"
            onClick={() => {
              const { callJoin, callType, callChannelId } = useChatManagerContext();
              callJoin && callJoin({ type: callType, channel_id: callChannelId });
            }}
          >
            Accept
          </button>
        </div>
      </div>
    );
  }

  // 3. 已接通，双方一致展示
  return (
    <div className="audio-call-window">
      <div className="audio-avatar">{renderAvatar()}</div>
      <div className="audio-call-info">
        <div className="audio-call-name">{currentChannel?.channel_display_name || "Unknown"}</div>
        <div className="audio-call-duration">{callDuration}</div>
      </div>
      <div className="audio-call-ops">
        <button className={`audio-btn audio-btn-mic${micEnabled ? "" : " off"}`} onClick={toggleMic}>
          {micEnabled ? <Mic size={22} /> : <MicOff size={22} />}
        </button>
        <button className="audio-btn audio-btn-hangup" onClick={handleHangup}>
          <Phone />
        </button>
      </div>
    </div>
  );
}
