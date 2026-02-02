import BaseService from "./BaseService";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, PAGINATION } from "lib/Constants";

import { isEmpty } from "lib/utils/Utils";

const { FIRST_PAGE } = PAGINATION;

const { PDF } = ALLOWED_FILE_FORMATS;

export class ServicePlanService extends BaseService {
  find({ clientId, searchText, page = FIRST_PAGE, sort, size = 10 }) {
    return super.request({
      url: `/clients/${clientId}/service-plans`,
      mockParams: { clientId },
      params: { searchText, page: page - 1, sort, size },
    });
  }

  isAnyInDevelopment(clientId) {
    return super.request({
      url: `/clients/${clientId}/service-plans/any-in-development`,
    });
  }

  findById(clientId, planId) {
    return super.request({
      url: `/clients/${clientId}/service-plans/${planId}`,
      mockParams: { id: planId },
    });
  }

  findHistory({ name, clientId, planId, page = FIRST_PAGE, size = 10 }) {
    return super.request({
      url: `/clients/${clientId}/service-plans/${planId}/history`,
      mockParams: { clientId },
      params: { name, page: page - 1, size },
    });
  }

  count({ clientId, ...params }, options) {
    return super.request({
      url: `/clients/${clientId}/service-plans/count`,
      mockParams: { id: clientId },
      ...options,
      params,
    });
  }

  findDomains(clientId, servicePlanId, params) {
    return super.request({
      url: `/clients/${clientId}/service-plans/${servicePlanId}/domains`,
      params,
    });
  }

  download(clientId, planId, params) {
    return super.request({
      type: ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
      url: `/clients/${clientId}/service-plans/${planId}/download`,
      params,
    });
  }

  save({ isAutoSave, ...data }, { clientId }) {
    const isNew = isEmpty(data.id);
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";

    const headers = !isAutoSave
      ? {}
      : {
          "X-Auth-With-Cookies": "no-update",
          // Authorization: AuthorizationData,
        };
    return super.request({
      method: isNew ? "POST" : "PUT",
      url: `/clients/${clientId}/service-plans`,
      body: data,
      type: "json",
      headers,
    });
  }

  canView({ clientId }, options) {
    return super.request({
      url: `/clients/${clientId}/service-plans/can-view`,
      ...options,
    });
  }

  canAdd({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/service-plans/can-add`,
    });
  }

  canReviewByClinician({ clientId }, options) {
    return super.request({
      url: `/clients/${clientId}/service-plans/can-review-by-clinician`,
      ...options,
    });
  }

  findControlled(clientId) {
    return super.request({
      url: `/clients/${clientId}/service-plans/controlled`,
      response: { extractDataOnly: true },
    });
  }

  findResourceNames(clientId) {
    return super.request({
      url: `/clients/${clientId}/service-plans/controlled/resource-names`,
    });
  }
}

const service = new ServicePlanService();
export default service;
