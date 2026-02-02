// src/lib/updateChannelListLastMessage.js
import { format } from "date-fns";

/**
 * 更新 channelList 某个 channel 的 last_message 和 updated_at
 * @param {QueryClient} queryClient
 * @param {string|number} channelId
 * @param {object} lastMessage
 */
export function updateChannelListLastMessage(queryClient, channelId, lastMessage) {
  queryClient.setQueryData(["channelList"], (oldList) => {
    if (!oldList) return oldList;
    return oldList.map((ch) => {
      if (ch.channel_id !== channelId) return ch;
      const updated_at = format(new Date(), "hh:mm a");
      return {
        ...ch,
        last_message: lastMessage,
        updated_at,
      };
    });
  });
}
