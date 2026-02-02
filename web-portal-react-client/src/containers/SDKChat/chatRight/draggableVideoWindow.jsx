import React, { useEffect, useLayoutEffect, useRef, useState } from "react";
import { Maximize, Minimize } from "lucide-react";

const DEFAULT_SIZE = { width: 720, height: 540 };
const MOBILE_SIZE = { width: 320, height: 480 };
const TABLET_SIZE = { width: 480, height: 640 };

const DraggableVideoWindow = ({ children }) => {
  const containerRef = useRef(null);
  const parentRef = useRef(null);
  const [pos, setPos] = useState({ x: 0, y: 0 });
  const [dragging, setDragging] = useState(false);
  const [offset, setOffset] = useState({ x: 0, y: 0 });
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  // ======== 响应式尺寸获取 =========
  const getResponsiveSize = () => {
    const screenWidth = window.innerWidth;
    const screenHeight = window.innerHeight;

    if (screenWidth <= 480) {
      // 手机屏幕
      return {
        width: Math.min(MOBILE_SIZE.width, screenWidth - 20),
        height: Math.min(MOBILE_SIZE.height, screenHeight - 60),
      };
    } else if (screenWidth <= 768) {
      // 平板屏幕
      return {
        width: Math.min(TABLET_SIZE.width, screenWidth - 40),
        height: Math.min(TABLET_SIZE.height, screenHeight - 80),
      };
    } else {
      // 桌面屏幕
      return DEFAULT_SIZE;
    }
  };

  // ======== 居中功能的核心部分 =========
  // 计算并设置窗口居中位置
  const centerWindow = () => {
    const parent = containerRef.current?.parentNode;
    if (!parent) return;
    const parentRect = parent.getBoundingClientRect();
    const size = getResponsiveSize();
    const winWidth = size.width;
    const winHeight = size.height;
    const x = Math.max(0, (parentRect.width - winWidth) / 2);
    const y = Math.max(0, (parentRect.height - winHeight) / 2);
    setPos({ x, y });
  };

  // 初次挂载和容器尺寸变化时居中
  useLayoutEffect(() => {
    // 检测屏幕尺寸变化
    const handleResize = () => {
      const newIsMobile = window.innerWidth <= 768;
      setIsMobile(newIsMobile);
      if (!isFullscreen) {
        centerWindow();
      }
    };

    centerWindow();

    // 监听窗口尺寸变化
    window.addEventListener("resize", handleResize);

    // 监听父节点尺寸变化
    const parent = containerRef.current?.parentNode;
    if (!parent) return;
    parentRef.current = parent;

    let resizeObserver = new window.ResizeObserver(() => {
      if (!isFullscreen) centerWindow();
    });
    resizeObserver.observe(parent);

    return () => {
      window.removeEventListener("resize", handleResize);
      resizeObserver.disconnect();
    };
    // eslint-disable-next-line
  }, []);

  // ========== 拖拽 ===========
  const onMouseDown = (e) => {
    if (isFullscreen || isMobile) return; // 移动端禁用拖拽
    const rect = containerRef.current.getBoundingClientRect();
    if (e.clientY - rect.top > 40) return;
    setDragging(true);
    setOffset({ x: e.clientX - rect.left, y: e.clientY - rect.top });
    e.stopPropagation();
    e.preventDefault();
  };

  // 触摸事件处理
  const onTouchStart = (e) => {
    if (isFullscreen || !isMobile) return;
    const rect = containerRef.current.getBoundingClientRect();
    const touch = e.touches[0];
    if (touch.clientY - rect.top > 40) return;
    setDragging(true);
    setOffset({ x: touch.clientX - rect.left, y: touch.clientY - rect.top });
    e.stopPropagation();
    e.preventDefault();
  };

  const onMouseMove = (e) => {
    if (!dragging || isFullscreen) return;
    const parentRect = containerRef.current.parentNode.getBoundingClientRect();
    let newX = e.clientX - parentRect.left - offset.x;
    let newY = e.clientY - parentRect.top - offset.y;
    const winWidth = containerRef.current.offsetWidth;
    const winHeight = containerRef.current.offsetHeight;
    const maxX = parentRect.width - winWidth;
    const maxY = parentRect.height - winHeight;
    newX = Math.max(0, Math.min(maxX, newX));
    newY = Math.max(0, Math.min(maxY, newY));
    setPos({ x: newX, y: newY });
  };

  const onTouchMove = (e) => {
    if (!dragging || isFullscreen || !isMobile) return;
    const parentRect = containerRef.current.parentNode.getBoundingClientRect();
    const touch = e.touches[0];
    let newX = touch.clientX - parentRect.left - offset.x;
    let newY = touch.clientY - parentRect.top - offset.y;
    const winWidth = containerRef.current.offsetWidth;
    const winHeight = containerRef.current.offsetHeight;
    const maxX = parentRect.width - winWidth;
    const maxY = parentRect.height - winHeight;
    newX = Math.max(0, Math.min(maxX, newX));
    newY = Math.max(0, Math.min(maxY, newY));
    setPos({ x: newX, y: newY });
  };

  const onMouseUp = () => setDragging(false);
  const onTouchEnd = () => setDragging(false);

  useEffect(() => {
    if (dragging) {
      document.addEventListener("mousemove", onMouseMove);
      document.addEventListener("mouseup", onMouseUp);
      document.addEventListener("touchmove", onTouchMove);
      document.addEventListener("touchend", onTouchEnd);
    } else {
      document.removeEventListener("mousemove", onMouseMove);
      document.removeEventListener("mouseup", onMouseUp);
      document.removeEventListener("touchmove", onTouchMove);
      document.removeEventListener("touchend", onTouchEnd);
    }
    return () => {
      document.removeEventListener("mousemove", onMouseMove);
      document.removeEventListener("mouseup", onMouseUp);
      document.removeEventListener("touchmove", onTouchMove);
      document.removeEventListener("touchend", onTouchEnd);
    };
    // eslint-disable-next-line
  }, [dragging, offset, isFullscreen, isMobile]);

  // ========== 放大/缩小 ===========
  const handleToggleFullscreen = () => {
    setIsFullscreen((prev) => !prev);
    if (isFullscreen) {
      // 恢复小窗时再次居中
      setTimeout(centerWindow, 0);
    }
  };

  // ========== 样式 ===========
  const getWindowSize = () => {
    if (isFullscreen) {
      return { width: "100%", height: "100%" };
    }
    const size = getResponsiveSize();
    return { width: size.width, height: size.height };
  };

  const windowSize = getWindowSize();

  const windowStyle = isFullscreen
    ? {
        position: "absolute",
        left: 0,
        top: 0,
        width: windowSize.width,
        height: windowSize.height,
        background: "transparent",
        boxShadow: "none",
        borderRadius: 0,
        zIndex: 999,
        display: "flex",
        flexDirection: "column",
        transition: "all 0.2s",
        overflow: "hidden",
      }
    : {
        position: "absolute",
        left: pos.x,
        top: pos.y,
        width: windowSize.width,
        height: windowSize.height,
        background: "transparent",
        boxShadow: "0 2px 12px rgba(0,0,0,0.2)",
        borderRadius: isMobile ? 4 : 8,
        zIndex: 999,
        display: "flex",
        flexDirection: "column",
        transition: "all 0.2s",
        overflow: "hidden",
      };

  return (
    <div
      ref={containerRef}
      className="draggableVideoWindow"
      style={windowStyle}
      onMouseDown={onMouseDown}
      onTouchStart={onTouchStart}
    >
      {!isMobile && (
        <div
          style={{
            position: "absolute",
            top: 8,
            left: 10,
            zIndex: 999,
            background: "#1890ff",
            borderRadius: 6,
            padding: 3,
            cursor: "pointer",
            display: "flex",
            alignItems: "center",
          }}
          onClick={(e) => {
            e.stopPropagation();
            handleToggleFullscreen();
          }}
          title={isFullscreen ? "Restore Window" : "Enlarge"}
        >
          {isFullscreen ? <Minimize size={20} color="#fff" /> : <Maximize size={20} color="#fff" />}
        </div>
      )}
      <div style={{ flex: 1, minHeight: 0, position: "relative" }}>{children}</div>
    </div>
  );
};

export default DraggableVideoWindow;
