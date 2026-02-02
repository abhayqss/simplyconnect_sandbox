// src/utils/processMessage.js
import { Base64 } from "js-base64";
import { format, parseISO } from "date-fns";

/**
 * 格式化 sender_info 字段
 */
function formatSenderInfo(senderInfo) {
  const firstName = (senderInfo?.first_name || "").trim();
  const lastName = (senderInfo?.last_name || "").trim();
  const avatar = (firstName[0] || "").toUpperCase() + (lastName[0] || "").toUpperCase();
  const display_name = [firstName, lastName].filter(Boolean).join(" ");
  return {
    ...senderInfo,
    avatar,
    display_name,
  };
}

/**
 * 统一处理单条消息
 * @param {Object} msg
 * @param {Object} [options]
 * @param {boolean} [options.decodeText=true] // 是否decode text
 * @param {boolean} [options.setShowTime=true] // 是否添加showTime字段
 * @returns {Object}
 */
export default function processMessage(msg, options = {}) {
  const { decodeText = true, setShowTime = true, forChannelList = false } = options;
  if (!msg || typeof msg !== "object") return msg;

  if (msg.type === "date-separator" || msg.type === "member_deleted") return msg;

  let text = msg.text;

  if (decodeText && msg.type === "text" && text) {
    try {
      text = Base64.decode(text);
    } catch (e) {
      // 忽略错误，原样返回
    }
  }

  if (forChannelList) {
    const isAudio = msg.additional === "1";
    const isVideo = msg.additional === "2";
    if (msg.type === "create") text = "New Chat";
    if (msg.type === "photo") text = "Photo";
    if (msg.type === "voice") text = "Voice";
    if (msg.type === "autio") text = "Voice";
    if (msg.type === "video_cancel")
      text = isAudio ? "Audio Cancelled" : isVideo ? "Video Cancelled" : "Call Cancelled";
    if (msg.type === "video_start") text = "Video Start";
    if (msg.type === "video_reject") text = isAudio ? "Audio Rejected" : isVideo ? "Video Rejected" : "Call Rejected";
    if (msg.type === "video_leave") text = msg.text ? "Call time: " + msg.text : "Call ended";
  } else {
    if (msg.type === "create") text = "New Chat";
    if (msg.type === "photo") text = "Photo";
    if (msg.type === "audio") text = "Voice";
    if (msg.type === "voice") text = "Voice";
    if (msg.type === "video_leave") text = "Call time : " + msg.text;
  }

  let showTime = msg.showTime;
  if (setShowTime && msg.created_at) {
    try {
      showTime = format(parseISO(msg.created_at), "hh:mm a");
    } catch {}
  }

  return {
    ...msg,
    text,
    ...(setShowTime ? { showTime } : {}),
    sender_info: formatSenderInfo(msg.sender_info || {}),
  };
}
