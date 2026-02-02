import { Base64 } from "js-base64";

// 处理发送消息格式
function buildMessage({
  text,
  channel_id,
  receiver,
  sender,
  file = "",
  additional = "",
  type = "text",
  file_size = 0,
}) {
  return {
    additional,
    channel_id,
    created_at: new Date().toISOString(),
    file,
    id: crypto.randomUUID(),
    message_id: crypto.randomUUID(),
    receiver,
    sender,
    text: Base64.encode(text), // 用库进行Base64编码
    type,
    file_size,
  };
}

export default buildMessage;
