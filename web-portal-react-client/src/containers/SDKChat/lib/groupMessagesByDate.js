import { compareAsc, format, parseISO } from "date-fns";
import processMessage from "./processMessage";

/**
 * 按 created_at 日期分组并扁平化，插入日期分割条
 * @param {Array} messageList 原始消息数组
 * @returns {Array} 扁平化数组，包含日期分割对象和增强后的消息对象
 */
function groupMessagesByDate(messageList) {
  if (!Array.isArray(messageList)) return [];

  // 升序排序
  const sorted = [...messageList].sort((a, b) => compareAsc(parseISO(a.created_at), parseISO(b.created_at)));

  // 分组
  const dateMap = {};
  sorted.forEach((msg) => {
    // 只为有 created_at 的消息分组
    if (msg.created_at) {
      const dateStr = format(parseISO(msg.created_at), "MM/dd/yyyy");
      if (!dateMap[dateStr]) dateMap[dateStr] = [];
      dateMap[dateStr].push(msg);
    }
  });

  // 日期升序拼接成扁平数组
  const result = [];
  Object.keys(dateMap)
    .sort((a, b) => compareAsc(parseISO(a), parseISO(b)))
    .forEach((dateStr) => {
      result.push({ type: "date-separator", date: dateStr });
      result.push(...dateMap[dateStr].map((msg) => processMessage(msg)));
    });
  return result;
}

export default groupMessagesByDate;
