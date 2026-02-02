import processMessage from "../processMessage";

export function handleMessage(data, updateChannelList, addMessageToChannel) {
  const msg = processMessage(data);
  const msgForChannelList = processMessage(data, { forChannelList: true });
  // 更新 列表中的lastMessage
  updateChannelList((channels) =>
    channels?.map((ch) =>
      ch.channel_id === msg.channel_id
        ? { ...ch, last_message: msgForChannelList, last_message_id: msg.message_id, updated_at: msg.showTime }
        : ch,
    ),
  );
  // 更新 allChannelData
  addMessageToChannel(msg.channel_id, msg);
}
