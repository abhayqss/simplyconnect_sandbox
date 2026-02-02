// useSendMessage.js
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useChatManagerContext } from "../context/ChatManagerContext";
import sendMessageFn from "../api/sendMessageFn"; // 你的实际 API
import processMessage from "../lib/processMessage";
import { updateChannelListLastMessage } from "../lib/updateChannelListLastMessage";

export default function useSendMessage(currentChannelId, onSuccess) {
  const { addMessageToChannel, setSearchText } = useChatManagerContext();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params) => {
      if (params.type === "text" && (!params.text || params.text.trim() === "")) {
        return Promise.reject(new Error("消息内容不能为空"));
      }
      return sendMessageFn(currentChannelId, params);
    },
    onSuccess: (msg) => {
      const processedNewMessage = processMessage(msg);

      // 1. 更新allChannelData
      addMessageToChannel(currentChannelId, processedNewMessage);

      // 2. 更新channelList的last_message
      updateChannelListLastMessage(queryClient, currentChannelId, { ...processedNewMessage, forChannelList: true });

      setSearchText("");

      if (onSuccess) onSuccess(msg);
    },
  });
}
