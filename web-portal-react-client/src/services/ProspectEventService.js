import { isEmpty } from "lib/utils/Utils";
import { PAGINATION } from "lib/Constants";

import BaseService from "./BaseService";

const { FIRST_PAGE } = PAGINATION;

export class ProspectEventService extends BaseService {
  find({ prospectId, page = FIRST_PAGE, size = 15, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/events`,
      params: { page, size, ...params },
    });
  }

  findById(eventId, { prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/events/${eventId}`,
      response: { extractDataOnly: true },
      params,
    });
  }

  count({ prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/events/count`,
      response: { extractDataOnly: true },
      params,
    });
  }

  save({ isAutoSave, ...data }, { prospectId }) {
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";

    const headers = !isAutoSave
      ? {}
      : {
          "X-Auth-With-Cookies": "no-update",
          // Authorization: AuthorizationData,
        };

    return super.request({
      method: isEmpty(data.id) ? "POST" : "PUT",
      url: `/prospects/${prospectId}/events`,
      body: data,
      type: "multipart/form-data",
      response: { extractDataOnly: true },
      headers,
    });
  }

  canView({ prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/events/can-view`,
      response: { extractDataOnly: true },
      params,
    });
  }

  canAdd({ prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/events/can-add`,
      response: { extractDataOnly: true },
      params,
    });
  }

  canEdit({ prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/events/can-edit`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findPageNumber({ eventId, prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/events/${eventId}/page-number`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findEventNotifications({ eventId, prospectId, page = FIRST_PAGE, size = 10, ...other }) {
    return super.request({
      url: `/prospects/${prospectId}/events/${eventId}/notifications`,
      params: { page: page - 1, size, ...other },
    });
  }
}

const service = new ProspectEventService();
export default service;
