import React, { useEffect, useRef, useState } from "react";
import "./callInviteModal.scss";
import { useChatManagerContext } from "../context/ChatManagerContext";
import ringMp3 from "../assets/ring.mp3";
import ellipsisLoader from "../assets/ellipsis-loader.svg";
import { Phone, PhoneOff } from "lucide-react";
import callReject from "../api/callReject";

const CallInviteModal = () => {
  const { callInvite, callType, callChannelId, callJoin } = useChatManagerContext();
  const audioRef = useRef(null);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);
  const [isSmallScreen, setIsSmallScreen] = useState(window.innerWidth <= 480);
  const [isLandscape, setIsLandscape] = useState(window.innerHeight < window.innerWidth);

  // 拒绝通话
  const handleRejectInvite = async (e) => {
    e?.preventDefault();
    e?.stopPropagation();
    const params = {
      type: callType,
      channel_id: callChannelId,
    };

    await callReject(params);
  };

  const handleJoinCall = async (e) => {
    e?.preventDefault();
    e?.stopPropagation();
    const params = {
      type: callType,
      channel_id: callChannelId,
    };

    await callJoin(params);
  };

  // 触摸事件处理
  const handleTouchStart = (e, action) => {
    e.preventDefault();
    e.stopPropagation();
    // 添加触摸反馈
    const button = e.currentTarget;
    button.style.transform = "scale(0.95)";
    button.style.transition = "transform 0.1s";
  };

  const handleTouchEnd = (e, action) => {
    e.preventDefault();
    e.stopPropagation();
    // 恢复按钮状态
    const button = e.currentTarget;
    button.style.transform = "scale(1)";

    // 执行对应的操作
    if (action === "accept") {
      handleJoinCall();
    } else if (action === "reject") {
      handleRejectInvite();
    }
  };

  // 监听屏幕尺寸变化
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth <= 768);
      setIsSmallScreen(window.innerWidth <= 480);
      setIsLandscape(window.innerHeight < window.innerWidth);
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    if (callInvite) {
      audioRef.current = new Audio(ringMp3);
      audioRef.current.loop = true;
      audioRef.current.play().catch((err) => {
        // 失败了，啥都不做
        console.log("auto play failed", err);
      });
    } else {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
        audioRef.current = null;
      }
    }
    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
        audioRef.current = null;
      }
    };
  }, [callInvite]);

  if (!callInvite) return null;

  // 获取 sender_info
  const sender = callInvite.sender_info || {};

  // 获取首字母大写
  const getInitials = (first, last) => {
    const f = first ? first[0].toUpperCase() : "";
    const l = last ? last[0].toUpperCase() : "";
    return f + l;
  };

  // 头像处理逻辑
  const showInitials = !callInvite.channel_head || callInvite.channel_head === "";
  const initials = getInitials(sender.first_name, sender.last_name);

  // 获取响应式图标尺寸
  const getIconSize = () => {
    if (window.innerWidth <= 320) return 20;
    if (window.innerWidth <= 480) return 24;
    return 28;
  };

  return (
    <div
      className={`video-invite-modal ${isMobile ? "mobile" : ""} ${isLandscape ? "landscape" : ""} ${isSmallScreen ? "fullscreen-mobile" : ""}`}
    >
      <div className="modal-content">
        <div className="avatar">
          {showInitials ? (
            <div className="avatar-initials">{initials}</div>
          ) : (
            <img src={callInvite.channel_head} alt="Avatar" />
          )}
        </div>
        <div className="info">
          <div className="name">{(sender.first_name || "") + " " + (sender.last_name || "")}</div>
          <div className="tip">
            Calling
            <img src={ellipsisLoader} alt="loading" className="ellipsis-loader" />
          </div>
        </div>
        <div className="actions">
          <button
            className="accept"
            onClick={handleJoinCall}
            onTouchStart={(e) => handleTouchStart(e, "accept")}
            onTouchEnd={(e) => handleTouchEnd(e, "accept")}
            aria-label="Accept call"
          >
            <Phone size={getIconSize()} color="#fff" />
          </button>
          <button
            className="reject"
            onClick={handleRejectInvite}
            onTouchStart={(e) => handleTouchStart(e, "reject")}
            onTouchEnd={(e) => handleTouchEnd(e, "reject")}
            aria-label="Reject call"
          >
            <PhoneOff size={getIconSize()} color="#fff" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default CallInviteModal;
