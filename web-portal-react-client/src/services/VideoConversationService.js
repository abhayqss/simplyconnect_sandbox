import { PAGINATION } from "lib/Constants";

import BaseService from "./BaseService";

const { FIRST_PAGE } = PAGINATION;

const baseService = new BaseService();

class VideoConversationService {
  #provider = null;

  constructor(p) {
    this.#provider = p;
  }

  setProvider(p) {
    this.#provider = p;
  }

  connect(params) {
    return this.#provider.connect(params);
  }

  disconnect(room) {
    return this.#provider.disconnect(room);
  }

  // 发起video的请求
  initiateCall({ conversationSid, employeeIds, incidentReportId }) {
    return baseService.request({
      method: "POST",
      url: incidentReportId ? "/conversations/video/incident-reports/call" : "/conversations/video",
      body: { conversationSid, employeeIds, incidentReportId },
      response: { extractDataOnly: true },
    });
  }

  declineCall(roomSid) {
    return baseService.request({
      method: "POST",
      url: "/conversations/video/decline",
      body: { roomSid },
    });
  }

  cancelCall(roomSid) {
    return baseService.request({
      method: "POST",
      url: "/conversations/video/cancel",
      body: { roomSid },
    });
  }

  canStartCall(params) {
    return baseService.request({
      params,
      response: { extractDataOnly: true },
      url: "/conversations/video/can-start",
    });
  }

  addParticipants(params) {
    return baseService.request({
      method: "POST",
      url: "/conversations/video/participants",
      body: params,
    });
  }

  findClientCareTeamMembers(params) {
    return baseService.request({
      url: "/conversations/video/participating/client-care-team-members",
      response: { extractDataOnly: true },
      params,
    });
  }

  findCommunityCareTeamMembers(params) {
    return baseService.request({
      url: "/conversations/video/participating/community-care-team-members",
      response: { extractDataOnly: true },
      params,
    });
  }

  findHistory({ page = FIRST_PAGE, sort, size = 10, ...other }) {
    return baseService.request({
      url: "/conversations/video/history",
      params: { page: page - 1, sort, size, ...other },
    });
  }

  createLocalTrack(type) {
    return this.#provider.createLocalTrack(type);
  }

  stopAllTracks(room) {
    return this.#provider.stopAllTracks(room);
  }
}

export default VideoConversationService;
