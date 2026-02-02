import { useAuthenticatedWebSocket } from "./useAuthenticatedWebSocket";
import { useChatManagerContext } from "../context/ChatManagerContext";
import { useEffect } from "react";
import { changeUserOnline } from "../lib/ws/changeUserOnline";
import { handleMessage } from "../lib/ws/handleMessage";
import { useQueryClient } from "@tanstack/react-query";
import { updateChannelListLastMessage } from "../lib/updateChannelListLastMessage";
import processMessage from "../lib/processMessage";

export function useSyncSocketToQuery() {
  const { messages } = useAuthenticatedWebSocket();
  const queryClient = useQueryClient();
  const {
    updateChannelList,
    addMessageToChannel,
    handleUnreadMessage,
    refreshChannelList,
    currentChannelId,
    switchToNextValidChannel,
    setCallInvite,
    setCallType,
    setCallChannelId,
    clearCallStatus,
    setIsCaller,
  } = useChatManagerContext();

  const handlers = {
    user_online: (data) => {
      changeUserOnline(data, updateChannelList, true);
    },
    user_offline: (data) => {
      changeUserOnline(data, updateChannelList, false);
    },
    text: (data) => {
      handleMessage(data, updateChannelList, addMessageToChannel);
      // 处理未读
      handleUnreadMessage(data);
    },
    create: () => {
      refreshChannelList();
    },
    member_deleted: (data) => {
      refreshChannelList();
      if (data.channel_id === currentChannelId) {
        // 调用切换逻辑，切到下一个存在的会话
        switchToNextValidChannel();
      }
    },
    channel_exit: () => {
      refreshChannelList();
    },
    new_member_join_in: () => {
      refreshChannelList();
    },
    video_cancel: (data) => {
      // 1. 先处理消息
      handleMessage(data, updateChannelList, addMessageToChannel);
      // 2. 然后局部更新channelList
      const processedNewMessage = processMessage(data, { forChannelList: true });
      updateChannelListLastMessage(queryClient, data.channel_id, processedNewMessage);

      // 双方都会收到，对方需要关闭等待弹窗
      clearCallStatus();
    },
    video_start: (data) => {
      // 对方 视频等待，
      setCallInvite(data);
      setCallType(data.video_type);
      setCallChannelId(data.channel_id);
      // 被叫方
      setIsCaller(false);
    },
    video_leave: (data) => {
      // 1. 先处理消息
      handleMessage(data, updateChannelList, addMessageToChannel);
      // 2. 然后局部更新channelList
      const processedNewMessage = processMessage(data, { forChannelList: true });
      updateChannelListLastMessage(queryClient, data.channel_id, processedNewMessage);

      // 双方都会收到，对方需要关闭通话相关弹窗
      clearCallStatus();
    },
    video_reject: (data) => {
      // 1. 先处理消息
      handleMessage(data, updateChannelList, addMessageToChannel);
      // 2. 然后局部更新channelList
      const processedNewMessage = processMessage(data, { forChannelList: true });
      updateChannelListLastMessage(queryClient, data.channel_id, processedNewMessage);

      // 双方都会收到，对方需要关闭等待弹窗
      clearCallStatus();
    },
  };

  useEffect(() => {
    if (!messages.length) return;
    const message = messages[messages.length - 1];
    if (!message?.type) return;
    const handler = handlers[message.type] || handlers.text;
    handler(message);
  }, [messages]);
}
