import React, { createContext, useContext, useState, useEffect, useRef } from "react";
import { useLocation } from "react-router-dom";

const UNREAD_CHATS_KEY = "sdkChatUnreadCount";
const ChatBadgeContext = createContext();

export const useChatBadge = () => useContext(ChatBadgeContext);

export function ChatBadgeProvider({ children }) {
  const location = useLocation();
  const locationRef = useRef(location.pathname); // Create a ref to hold the latest location

  // Keep the ref updated with the latest location pathname
  useEffect(() => {
    locationRef.current = location.pathname;
  }, [location.pathname]);

  // 初始化为localStorage中的值
  const [unreadChatsCount, setUnreadChatsCount] = useState(() => {
    const saved = localStorage.getItem(UNREAD_CHATS_KEY);
    return saved !== null ? parseInt(saved, 10) : 0;
  });

  // 同步localStorage
  useEffect(() => {
    localStorage.setItem(UNREAD_CHATS_KEY, unreadChatsCount);
  }, [unreadChatsCount]);

  const incrementUnread = () => {
    if (locationRef.current === "/web-portal/chats") {
      setUnreadChatsCount(0);
      return;
    }
    setUnreadChatsCount((c) => c + 1);
  };

  const clearUnread = () => setUnreadChatsCount(0);

  return (
    <ChatBadgeContext.Provider value={{ unreadChatsCount, incrementUnread, clearUnread }}>
      {children}
    </ChatBadgeContext.Provider>
  );
}
