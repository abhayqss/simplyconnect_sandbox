import { createContext, useContext } from "react";
import { useChatManager } from "../hook/useChatManager";

export const ChatManagerContext = createContext(null);

export const useChatManagerContext = () => {
  const context = useContext(ChatManagerContext);
  if (!context) {
    throw new Error("useChatManagerContext must be used within a ChatManagerProvider");
  }
  return context;
};

export const ChatManagerProvider = ({ children }) => {
  const chatManager = useChatManager();
  return <ChatManagerContext.Provider value={chatManager}>{children}</ChatManagerContext.Provider>;
};
