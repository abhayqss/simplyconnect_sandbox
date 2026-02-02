import React, { useState, useEffect } from "react";
import DraggableVideoWindow from "./draggableVideoWindow";
import CallInviteModal from "./callInviteModal";
import { useChatManagerContext } from "../context/ChatManagerContext";

export default function CallInviteModalWrapper(props) {
  const { callInvite, callToken } = useChatManagerContext();
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);
  const [isSmallScreen, setIsSmallScreen] = useState(window.innerWidth <= 480);

  // 监听屏幕尺寸变化
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth <= 768);
      setIsSmallScreen(window.innerWidth <= 480);
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  if (!callInvite || callToken) {
    return null;
  }

  // 在移动端小屏幕上，直接全屏渲染模态框，不需要包装器
  if (isSmallScreen) {
    return <CallInviteModal {...props} />;
  }

  return (
    <DraggableVideoWindow>
      <CallInviteModal {...props} />
    </DraggableVideoWindow>
  );
}
