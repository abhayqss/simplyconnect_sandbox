import { Image, Mic, SendHorizontal } from "lucide-react";
import { Input, Progress } from "reactstrap";
import buildMessage from "../lib/buildMessage";
import { useRef, useState } from "react";
import { useChatManagerContext } from "../context/ChatManagerContext";
import useSendMessage from "../hook/useSendMessage";
import { uploadFileToS3 } from "../lib/uploadFile";
import RecorderWithWaveform from "../lib/RecorderWithWaveform";
import { WarningDialog } from "../../../components/dialogs";

const ChatRightBottom = () => {
  const [inputValue, setInputValue] = useState("");
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadError, setUploadError] = useState("");
  const [showWarning, setShowWarning] = useState(false);
  const fileInputRef = useRef(null);
  const [showAudio, setShowAudio] = useState(false);
  const [audioBlob, setAudioBlob] = useState(null);
  const [isRecordingAudio, setIsRecordingAudio] = useState(false);

  const { currentChannelId, currentChannel, awsData, chatUserId } = useChatManagerContext();

  const sendMessageMutation = useSendMessage(currentChannelId, () => setInputValue(""));

  const handleInputChange = (e) => setInputValue(e.target.value);

  // 图片选择事件
  const handleImageClick = () => {
    if (fileInputRef.current) fileInputRef.current.value = null;
    fileInputRef.current?.click();
  };

  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setUploadError("");
    setUploading(true);
    setUploadProgress(0);

    try {
      const config = {
        bucketName: process.env.REACT_APP_CHAT_S3_BUCKETNAME,
        region: process.env.REACT_APP_CHAT_S3_REGION,
        accessKeyId: awsData.os_key,
        secretAccessKey: awsData.os_secret,
      };

      const res = await uploadFileToS3(file, (percent) => setUploadProgress(percent), config, "image");
      sendImageMessage(res.key);
    } catch (err) {
      setUploadError(err.message || "Image upload failed");
      setShowWarning(true);
    } finally {
      setUploading(false);
      setUploadProgress(0);
    }
  };

  // 发送图片消息
  const sendImageMessage = (imageUrl) => {
    if (!currentChannelId) return;
    const members = currentChannel?.channel_member || [];
    const otherMembers = members.filter((m) => m.user_id !== chatUserId);
    let receiver = "";
    if (members.length === 2 && otherMembers.length === 1) {
      receiver = otherMembers[0].user_id;
    }
    const msg = buildMessage({
      text: "",
      file: imageUrl,
      channel_id: currentChannelId,
      receiver,
      sender: chatUserId,
      type: "photo",
    });
    sendMessageMutation.mutate(msg);
  };

  // 核心：统一发送按钮逻辑
  const handleSend = async () => {
    const members = currentChannel?.channel_member || [];
    const otherMembers = members.filter((m) => m.user_id !== chatUserId);
    let receiver = "";
    if (members.length === 2 && otherMembers.length === 1) {
      receiver = otherMembers[0].user_id;
    }

    if (showAudio) {
      // 语音模式
      if (!audioBlob || !currentChannelId) return;

      setUploadError("");
      setUploading(true);
      setUploadProgress(0);

      try {
        const config = {
          bucketName: process.env.REACT_APP_CHAT_S3_BUCKETNAME,
          region: process.env.REACT_APP_CHAT_S3_REGION,
          accessKeyId: awsData.os_key,
          secretAccessKey: awsData.os_secret,
        };
        // 上传到S3
        const res = await uploadFileToS3(audioBlob, (percent) => setUploadProgress(percent), config, "audio");
        // 上传成功，发送消息
        const msg = buildMessage({
          text: "",
          file: res.key,
          file_size: audioBlob.size,
          channel_id: currentChannelId,
          receiver,
          sender: chatUserId,
          type: "voice", // 标记消息类型为音频
        });

        sendMessageMutation.mutate(msg);

        setAudioBlob(null);
        setShowAudio(false);
      } catch (err) {
        setUploadError(err.message || "voice upload failed");
        setShowWarning(true);
      } finally {
        setUploading(false);
        setUploadProgress(0);
      }
    } else {
      // 文本模式
      if (!inputValue.trim() || !currentChannelId) return;
      const msg = buildMessage({
        text: inputValue,
        channel_id: currentChannelId,
        receiver,
        sender: chatUserId,
      });
      sendMessageMutation.mutate(msg);
      setInputValue("");
    }
  };

  // 回车发送（仅文本模式）
  const handleKeyDown = (e) => {
    if (uploading) return;
    if (!showAudio && e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  // 录音完成回调
  const handleAudioReady = (blob) => {
    setAudioBlob(blob);
  };

  // 录音状态变更回调
  const handleRecordingStatusChange = (recording) => {
    setIsRecordingAudio(recording);
  };

  return (
    <div className="chatRightBottomWrapper">
      {/* 上传进度与错误提示 */}
      {uploading && (
        <div className="processBox">
          <Progress value={uploadProgress} style={{ marginBottom: 8 }}>
            {uploadProgress}%
          </Progress>
        </div>
      )}

      <WarningDialog
        isOpen={showWarning}
        title="Upload failed, please try again."
        text={<div style={{ textAlign: "center" }}>{uploadError}</div>}
        buttons={[
          {
            text: "OK",
            onClick: () => {
              setShowWarning(false);
              setUploadError("");
            },
          },
        ]}
      />

      <div className="chatRightBottom">
        <div className="bottomLeft">
          <div className="actionBox">
            <div className="imageBox" onClick={handleImageClick} style={{ cursor: "pointer" }}>
              <Image size={24} color="#0064ad" />
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                style={{ display: "none" }}
                onChange={handleImageChange}
              />
            </div>
            <div
              className="imageBox"
              onClick={() => {
                setShowAudio(!showAudio);
                setAudioBlob(null);
              }}
            >
              <Mic size={24} color="#0064ad" />
            </div>
          </div>
          {!showAudio ? (
            <Input
              className="chatInput"
              type="textarea"
              placeholder="Type a message..."
              value={inputValue}
              onChange={handleInputChange}
              onKeyDown={handleKeyDown}
            />
          ) : (
            <RecorderWithWaveform
              onAudioReady={handleAudioReady}
              onRecordingStatusChange={handleRecordingStatusChange}
            />
          )}
        </div>
        <div className="imageBox">
          <SendHorizontal
            color={uploading || (showAudio && (isRecordingAudio || !audioBlob)) ? "#d0d0d0" : "#898989"}
            size={24}
            onClick={uploading || (showAudio && (isRecordingAudio || !audioBlob)) ? undefined : handleSend}
            style={{
              cursor: uploading || (showAudio && (isRecordingAudio || !audioBlob)) ? "not-allowed" : "pointer",
              opacity: uploading || (showAudio && (isRecordingAudio || !audioBlob)) ? 0.4 : 1,
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default ChatRightBottom;
