import React, { useCallback, useEffect, useRef, useState } from "react";
import "./chatRight.scss";
import { useChatManagerContext } from "../context/ChatManagerContext";
import { ArrowDown, Loader2 } from "lucide-react";
import ImageWithLoading from "../lib/ImageWithLoading";
import AudioWithLoading from "../lib/AudioWithLoading";

function renderMessageContent(item) {
  switch (item.type) {
    case "text":
      return <span>{item.text}</span>;
    case "photo":
      const imgUrl = `https://${process.env.REACT_APP_CHAT_S3_BUCKETNAME}.s3.${process.env.REACT_APP_CHAT_S3_REGION}.amazonaws.com/${item.file}`;
      return <ImageWithLoading src={imgUrl} />;
    case "audio":
    case "voice":
      const audioUrl = item.file
        ? `https://${process.env.REACT_APP_CHAT_S3_BUCKETNAME}.s3.${process.env.REACT_APP_CHAT_S3_REGION}.amazonaws.com/${item.file}`
        : item.url || "";
      return audioUrl ? <AudioWithLoading src={audioUrl} /> : <span>[Audio loading failed]</span>;
    default:
      return <span>{item.text || "[Unknown message type]"}</span>;
  }
}

const videoTypeMap = (type, socketType) => {
  if (type === "2") {
    switch (socketType) {
      case "video_cancel":
        return `The video has been canceled.`;
      case "video_reject":
        return `The video has been rejected.`;
      case "video_leave":
        return "";
    }
  }

  if (type === "1") {
    switch (socketType) {
      case "video_cancel":
        return `The audio has been canceled.`;
      case "video_reject":
        return `The audio has been rejected.`;
      case "video_leave":
        return "";
    }
  }
};

const ChatRightContent = () => {
  const { currentChannel, loadMoreMessages, paginationState, currentChannelId, chatUserId } = useChatManagerContext();
  const messageListRef = useRef(null);

  // 是否显示“滚动到底部”按钮
  const [showScrollBottom, setShowScrollBottom] = useState(false);
  // 用户是否手动滚动过（影响自动滚动到底部）
  const [userManuallyScrolled, setUserManuallyScrolled] = useState(false);

  // 用于"加载更多"时保持滚动位置
  const [scrollPosition, setScrollPosition] = useState(null);
  // 防止滚动恢复时递归触发
  const isRestoringScroll = useRef(false);
  // 记录加载更多前的最后一条消息ID，用于精确定位
  const [lastVisibleMessageId, setLastVisibleMessageId] = useState(null);

  // 滚动事件
  const handleScroll = useCallback(() => {
    const el = messageListRef.current;
    if (!el) return;
    if (el.scrollHeight <= el.clientHeight + 20) {
      return;
    }
    if (el.scrollTop < 100 && paginationState.hasMore && !paginationState.isLoadingMore) {
      // 记录当前滚动位置和可见的第一条消息ID
      const visibleMessages = currentChannel?.message_list || [];
      const firstVisibleMessage = visibleMessages.find((msg) => msg.message_id && msg.type !== "date-separator");

      setScrollPosition({ height: el.scrollHeight, top: el.scrollTop });
      setLastVisibleMessageId(firstVisibleMessage?.message_id || null);
      loadMoreMessages();
    }
    if (isRestoringScroll.current) return;
    const { scrollTop, scrollHeight, clientHeight } = el;
    const distanceToBottom = scrollHeight - scrollTop - clientHeight;
    setShowScrollBottom(distanceToBottom > 100);
    setUserManuallyScrolled(distanceToBottom > 100);
  }, [paginationState, loadMoreMessages]);

  useEffect(() => {
    if (!scrollPosition || !messageListRef.current || paginationState.isLoadingMore) return;

    const el = messageListRef.current;

    // 优先使用消息ID定位
    if (lastVisibleMessageId) {
      const targetElement = el.querySelector(`[data-message-id="${lastVisibleMessageId}"]`);
      if (targetElement) {
        isRestoringScroll.current = true;
        // 滚动到目标消息位置，并向上偏移一些像素以显示部分上方内容
        const offsetTop = targetElement.offsetTop - 100;
        el.scrollTop = Math.max(0, offsetTop);
        isRestoringScroll.current = false;
        setScrollPosition(null);
        setLastVisibleMessageId(null);
        return;
      }
    }

    // 备用方案：使用高度差计算
    const newScrollHeight = el.scrollHeight;
    const heightDiff = newScrollHeight - scrollPosition.height;
    if (heightDiff > 0) {
      isRestoringScroll.current = true;
      // 调整滚动位置，向上偏移一些像素以显示更多上下文
      el.scrollTop = scrollPosition.top + heightDiff - 50;
      isRestoringScroll.current = false;
    }

    setScrollPosition(null);
    setLastVisibleMessageId(null);
  }, [scrollPosition, paginationState.isLoadingMore, currentChannel?.message_list, lastVisibleMessageId]);

  useEffect(() => {
    if (!messageListRef.current) return;
    if (!paginationState.isLoadingMore && !userManuallyScrolled) {
      messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
      setShowScrollBottom(false);
    }
  }, [currentChannel?.message_list, userManuallyScrolled, paginationState.isLoadingMore]);

  useEffect(() => {
    setUserManuallyScrolled(false);
    setShowScrollBottom(false);
    if (messageListRef.current) {
      messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
    }
  }, [currentChannelId]);

  if (!currentChannel) {
    return (
      <div className="chatRightContent">
        <div className="emptyState">Select a channel</div>
      </div>
    );
  }

  if (!currentChannel?.message_list?.length) {
    return (
      <div className="chatRightContent">
        <div className="emptyState">No messages yet</div>
      </div>
    );
  }

  return (
    <div className="chatRightContent" style={{ position: "relative" }}>
      {paginationState.isLoadingMore && (
        <div className="loadingMore">
          <Loader2 className="animate-spin" size={20} />
        </div>
      )}

      <div
        className="messageChat"
        ref={messageListRef}
        onScroll={handleScroll}
        style={{ overflowY: "auto", height: "100%" }}
      >
        {currentChannel.message_list.map((item, index) => {
          // 日期分隔
          if (item.type === "date-separator") {
            return (
              <div key={`date-${item.date}-${index}`} className="dateSeparator">
                {item.date}
              </div>
            );
          }
          // create 类型，居中显示
          if (item.type === "create") {
            return (
              <div key={`create-${item.message_id || index}`} className="centerTip">
                {item.text || "Channel created"}
              </div>
            );
          }
          // video_ 开头的类型，单独显示，video_join 不展示
          if (item.type && item.type.startsWith("video_")) {
            if (item.type === "video_join") {
              return null;
            }
            let videoText;
            if (item.type === "video_leave") {
              videoText = item.text || "";
            } else {
              videoText = videoTypeMap(item.additional, item.type) || "Video message";
            }
            return (
              <div key={`video-${item.message_id || index}`} className="videoTimeTip">
                {item.showTime && <div className="time">{item.showTime}</div>}
                <div>{videoText}</div>
              </div>
            );
          }

          // 普通消息
          const isCurrentUser = item.sender === chatUserId;
          const senderName = item.sender_info?.display_name || "Unknown";
          const avatarText = item.sender_info?.avatar || senderName.slice(0, 2).toUpperCase();

          return (
            <div
              key={`msg-${item.message_id || index}`}
              className={isCurrentUser ? "selfDialogueBox" : "anotherOneBox"}
              data-message-id={item.message_id}
            >
              {!isCurrentUser && (
                <div className="avatar" title={senderName}>
                  {avatarText}
                </div>
              )}
              <div className="messageBox">
                {!isCurrentUser && (
                  <div className="header">
                    <div className="name">{senderName}</div>
                    <div className="time">{item.showTime}</div>
                  </div>
                )}
                {isCurrentUser && <div className="time">{item.showTime}</div>}
                <div className={isCurrentUser ? "text" : "message"}>{renderMessageContent(item)}</div>
              </div>
            </div>
          );
        })}
      </div>

      {showScrollBottom && (
        <div
          className="scrollToBottomBtn"
          onClick={() => {
            setUserManuallyScrolled(false);
            messageListRef.current?.scrollTo({
              top: messageListRef.current.scrollHeight,
              behavior: "smooth",
            });
          }}
        >
          <ArrowDown size={20} />
          <span>Scroll to bottom</span>
        </div>
      )}
    </div>
  );
};

export default ChatRightContent;
