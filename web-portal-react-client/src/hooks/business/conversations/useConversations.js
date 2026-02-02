import { ConversationService } from "factories";

const service = ConversationService();

const api = {
  init: (options) => service.init(options),
  isReady: () => service.isReady(),
  create: (params) => service.create(params),
  shutdown: () => service.shutdown(),
  get: (params) => service.get(params),
  getAll: (params) => service.getAll(params),
  getBySid: (sid) => service.getBySid(sid),
  leaveBySid: (sid) => service.leaveBySid(sid),
  updateToken: () => service.updateToken(),
  addParticipants: (params) => service.addParticipants(params),
  getAllSubscribedUsers: () => service.getSubscribedUsers(),
  getByMessage: (message) => service.getByMessage(message),
  getUserProfiles: (params) => service.getUserProfiles(params),
  getUserByIdentity: (identity) => service.getUserByIdentity(identity),
  on: (type, listener) => service.on(type, listener),
  off: (type, listener) => service.off(type, listener),
  emit: (type, payload) => service.emit(type, payload),
  getMessages: (cv, params) => service.getMessages(cv, params),
  getMessagesCount: (cv) => service.getMessagesCount(cv),
  deleteParticipants: (sid, ids, managerId) => service.deleteParticipants(sid, ids, managerId),
  sendMessage: (cv, message, messageAttrs) => service.sendMessage(cv, message, messageAttrs),
  updateMessage: (cv, message, newMessage) => service.updateMessage(cv, message, newMessage),
  addMessageReaction: (data, params) => service.addMessageReaction(data, params),
  deleteMessageReaction: (data, params) => service.deleteMessageReaction(data, params),
  getParticipants: (cv) => service.getParticipants(cv),
  getConversationParticipant: (cv, identity) => service.getConversationParticipant(cv, identity),
  getUnreadMessageCount: (cv) => service.getUnreadMessageCount(cv),
  updateLastReadMessageIndex: (cv, index) => service.updateLastReadMessageIndex(cv, index),
  setAllMessagesRead: (cv) => service.setAllMessagesRead(cv),
};

export default function useConversations() {
  return api;
}
