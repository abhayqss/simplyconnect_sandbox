import { format } from "date-fns";
import processMessage from "./processMessage";

function formatTime(dateStr) {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return format(date, "hh:mm a");
  } catch {
    return "";
  }
}

// 处理单个成员
function processMember(member) {
  const first = (member.first_name || "").trim();
  const last = (member.last_name || "").trim();
  const user_name = [first, last].filter(Boolean).join(" ");

  // 只取前两个单词首字母大写
  const user_avatar = user_name
    .split(" ")
    .slice(0, 2) // 只保留前两个单词
    .map((word) => word.charAt(0).toUpperCase())
    .join("");

  return {
    ...member,
    user_name,
    user_avatar,
  };
}

const ProcessChannelList = (messages, chatUserId) => {
  if (!messages || !Array.isArray(messages) || !chatUserId) {
    return [];
  }

  return messages.map((message) => {
    const { channel, channel_member, ...otherMessageFields } = message;

    const { channel_id, channel_type, updated_at, user_id: channel_created_id, ...restChannelFields } = channel;

    // 处理成员列表
    const processedMembers = (channel_member || []).map(processMember);

    // 过滤除当前用户外的成员
    const otherMembers = processedMembers.filter((m) => m.user_id !== chatUserId);

    let channel_display_name;
    let channel_avatar;

    if (channel_type === 0) {
      // 单聊
      const other = otherMembers[0] || {};
      channel_display_name = other.user_name || "";
      channel_avatar = other.user_avatar || "";
    } else {
      // 群聊
      // 优先使用 display_name
      if (channel.display_name && channel.display_name.trim()) {
        channel_display_name = channel.display_name.trim();
      } else {
        let names = processedMembers.slice(0, 3).map((m) => m.user_name);
        channel_display_name = names.join(", ");
        if (processedMembers.length > 3) channel_display_name += "...";
      }
      channel_avatar = processedMembers
        .slice(0, 2)
        .map((m) => m.user_avatar)
        .join("");
    }

    const formattedUpdatedAt = formatTime(updated_at);

    let lastMessage = message.last_message ? { ...message.last_message } : undefined;
    if (lastMessage) {
      lastMessage = processMessage(lastMessage, { setShowTime: false, forChannelList: true });
    }

    return {
      ...otherMessageFields, // 保留 message 的其余字段
      ...restChannelFields,
      last_message: lastMessage,
      channel_id,
      channel_created_id,
      channel_type,
      channel_display_name,
      channel_avatar,
      updated_at: formattedUpdatedAt,
      channel_member: processedMembers, // 新加工后的成员列表
    };
  });
};

export default ProcessChannelList;
