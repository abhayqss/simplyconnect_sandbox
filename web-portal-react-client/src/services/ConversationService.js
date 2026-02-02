import BaseService from "./BaseService";

let provider = null;

const baseService = new BaseService();

export default class ConversationService {
  constructor(p) {
    provider = p;
  }

  setProvider(p) {
    provider = p;
  }

  init(options) {
    return provider?.init(options);
  }

  updateToken() {
    return provider?.updateToken();
  }

  isReady() {
    return provider?.isReady();
  }

  on(type, listener) {
    provider?.on(type, listener);
  }

  off(type, listener) {
    provider?.off(type, listener);
  }

  emit(type, payload) {
    provider?.emit(type, payload);
  }

  get(params) {
    return provider?.get(params);
  }

  getAll(params) {
    return provider?.getAll(params);
  }

  getBySid(sid) {
    return provider?.getBySid(sid) || "";
  }

  create(params) {
    return baseService.request({
      method: "POST",
      url: "/conversations",
      body: params,
    });
  }

  leaveBySid(sid) {
    return baseService.request({
      method: "POST",
      url: "/conversations/leave",
      type: "application/x-www-form-urlencoded",
      body: { conversationSid: sid },
    });
  }

  addParticipants(params) {
    return baseService.request({
      method: "POST",
      url: "/conversations/participants",
      body: params,
    });
  }

  shutdown() {
    return provider.shutdown();
  }

  getUserByIdentity(identity) {
    return provider.getUserByIdentity(identity);
  }

  getUserProfiles(params) {
    return baseService.request({
      method: "POST",
      url: "/conversations/users",
      type: "application/x-www-form-urlencoded",
      response: {
        extractDataOnly: true,
      },
      body: params,
    });
  }

  findOrganizations(params) {
    return baseService.request({
      url: "/conversations/participating/organizations",
      response: {
        extractDataOnly: true,
      },
      params,
    });
  }

  findCommunities(params) {
    return baseService.request({
      url: "/conversations/participating/communities",
      response: {
        extractDataOnly: true,
      },
      params,
    });
  }

  getParticipatingAccessibility(params) {
    return baseService.request({
      url: "/conversations/participating/accessibility",
      response: {
        extractDataOnly: true,
      },
      params,
    });
  }

  findClients(params) {
    return baseService.request({
      url: "/conversations/participating/clients",
      hasEmptyParams: true,
      response: {
        extractDataOnly: true,
      },
      params,
    });
  }

  findContacts(params) {
    return baseService.request({
      url: "/conversations/participating/contacts",
      hasEmptyParams: true,
      response: {
        extractDataOnly: true,
      },
      params,
    });
  }

  findClientCareTeamMembers(params) {
    return baseService.request({
      url: "/conversations/participating/client-care-team-members",
      response: { extractDataOnly: true },
      params,
    });
  }

  findCommunityCareTeamMembers(params) {
    return baseService.request({
      url: "/conversations/participating/community-care-team-members",
      response: { extractDataOnly: true },
      params,
    });
  }

  getMessages(conversation, { from, direction, pageSize = 15 } = {}) {
    return provider.getMessages(conversation, { from, direction, pageSize });
  }

  getMessagesCount(cv) {
    return provider.getMessagesCount(cv);
  }

  addMessageReaction({ reactionId }, { messageSid, conversationSid }) {
    return baseService.request({
      method: "POST",
      type: "application/x-www-form-urlencoded",
      url: `/conversations/${conversationSid}/messages/${messageSid}/reactions/${reactionId}`,
      response: { extractDataOnly: true },
    });
  }

  deleteMessageReaction({ reactionId }, { messageSid, conversationSid }) {
    return baseService.request({
      method: "DELETE",
      type: "application/x-www-form-urlencoded",
      url: `/conversations/${conversationSid}/messages/${messageSid}/reactions/${reactionId}`,
      response: { extractDataOnly: true },
    });
  }

  getParticipants(cv) {
    return provider.getParticipants(cv);
  }

  getConversationParticipant(cv, identity) {
    return provider.getConversationParticipant(cv, identity);
  }

  getByMessage(message) {
    return provider.getByMessage(message);
  }

  sendMessage(cv, message, messageAttrs) {
    return provider.sendMessage(cv, message, messageAttrs);
  }

  updateMessage(cv, message, newMessage) {
    return provider.updateMessage(cv, message, newMessage);
  }

  getUnreadMessageCount(cv) {
    return provider.getUnreadMessageCount(cv);
  }

  updateLastReadMessageIndex(cv, index) {
    return provider.updateLastReadMessageIndex(cv, index);
  }

  setAllMessagesRead(conversation) {
    return provider.setAllMessagesRead(conversation);
  }

  deleteParticipants(sid, ids, managerId) {
    return baseService.request({
      method: "DELETE",
      url: "/conversations/participants",
      body: {
        conversationSid: sid,
        removedEmployeeIds: ids,
        careteamManagerId: managerId || null,
      },
    });
  }

  sendToDocuTrack(data) {
    return baseService.request({
      method: "PUT",
      url: "/conversations/send-to-docutrack",
      body: data,
      response: { extractDataOnly: true },
    });
  }

  uploadFromDocuTrack(data) {
    return baseService.request({
      method: "PUT",
      url: "/conversations/attach-from-docutrack",
      body: data,
      response: { extractDataOnly: true },
    });
  }

  findLoginUserInGroup({ clientId, clinical }) {
    return baseService.request({
      method: "POST",
      url: `/conversations/${clientId}/checkCurrentUserInChat?clinical=${clinical}`,
    });
  }
}
