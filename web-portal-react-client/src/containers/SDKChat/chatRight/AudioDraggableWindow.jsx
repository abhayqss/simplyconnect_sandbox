import React from "react";
import Draggable from "react-draggable";
import "./audioDraggableWindow.scss";

/**
 * 仅用于音频页面的小弹窗拖拽
 * 支持父容器内自由拖动，无全屏/最小化按钮，不共享视频的弹窗逻辑
 */
export default function AudioDraggableWindow({ children }) {
  return (
    <Draggable
      defaultPosition={{x: 120, y: 120}}
      handle=".audio-draggable-window"
      bounds="body"
    >
      <div className="audio-draggable-window">
        {children}
      </div>
    </Draggable>
  );
}