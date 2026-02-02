import { useAuthenticatedWebSocket } from "./useAuthenticatedWebSocket";
import { useSyncSocketToQuery } from "./useSyncSocketToQuery";
import { useChatBadge } from "../context/ChatBadgeContext";
import { useChatManagerContext } from "../context/ChatManagerContext";

const InitSocket = () => {
  const { incrementUnread } = useChatBadge();
  const { chatToken } = useChatManagerContext();

  // 只在消息类型不是 user_online 和 user_offline 时递增未读数
  const onNewChatMessage = (msg) => {
    if (msg && msg.type !== "user_online" && msg.type !== "user_offline") {
      incrementUnread();
    }
  };

  // 仅在有chatToken时建立WebSocket连接
  useAuthenticatedWebSocket(chatToken, {
    reconnectInterval: 5000,
    maxReconnectAttempts: 3,
    onNewChatMessage,
  });

  // 始终调用useSyncSocketToQuery，但内部会检查chatToken
  useSyncSocketToQuery();

  return null;
};

export default InitSocket;
