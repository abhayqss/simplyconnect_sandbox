import { PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

import BaseService from "./BaseService";

const { FIRST_PAGE } = PAGINATION;

export class ProspectService extends BaseService {
  find({ page = FIRST_PAGE, ...params }) {
    return super.request({
      url: "/prospects",
      params: { page: page - 1, ...params },
    });
  }

  findById(prospectId, params) {
    return super.request({
      url: `/prospects/${prospectId}`,
      response: { extractDataOnly: true },
      params,
    });
  }

  count(params) {
    return super.request({
      url: "/prospects/count",
      response: { extractDataOnly: true },
      params,
    });
  }

  save({ isAutoSave, ...data }) {
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";
    const headers = !isAutoSave
      ? {}
      : {
          "X-Auth-With-Cookies": "no-update",
          // Authorization: AuthorizationData,
        };

    return super.request({
      method: isEmpty(data.id) ? "POST" : "PUT",
      url: "/prospects",
      body: data,
      type: "multipart/form-data",
      response: { extractDataOnly: true },
      headers,
    });
  }

  canView(params) {
    return super.request({
      url: "/prospects/can-view",
      response: { extractDataOnly: true },
      params,
    });
  }

  canAdd(params) {
    return super.request({
      url: "/prospects/can-add",
      response: { extractDataOnly: true },
      params,
    });
  }

  canEdit({ prospectId }) {
    return super.request({
      url: `/prospects/${prospectId}/can-edit`,
      response: { extractDataOnly: true },
    });
  }

  toggleStatus(prospectId) {
    return super.request({
      method: "POST",
      url: `/prospects/${prospectId}/toggle-status`,
      response: { extractDataOnly: true },
    });
  }

  activate({ prospectId, ...data }) {
    return super.request({
      method: "POST",
      url: `/prospects/${prospectId}/activate`,
      response: { extractDataOnly: true },
      body: data,
    });
  }

  deactivate({ prospectId, ...data }) {
    return super.request({
      method: "POST",
      url: `/prospects/${prospectId}/deactivate`,
      response: { extractDataOnly: true },
      body: data,
    });
  }

  findPrimaryContacts({ prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/prospective-primary-contacts`,
      response: { extractDataOnly: true },
      params,
    });
  }

  validateUniqInOrganization(params) {
    return super.request({
      url: "/prospects/validate-uniq-in-organization",
      response: { extractDataOnly: true },
      params,
    });
  }

  validateUniqInCommunity(params) {
    return super.request({
      url: "/prospects/validate-uniq-in-community",
      response: { extractDataOnly: true },
      params,
    });
  }

  findHouseholdMembers({ prospectId, ...params }) {
    return super.request({
      url: `/prospects/${prospectId}/household-members`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findEmergencyContacts(prospectId) {
    return super.request({
      url: `/prospects/${prospectId}/emergency-contacts`,
    });
  }

  findBillingDetails(prospectId) {
    return super.request({
      url: `/prospects/${prospectId}/billing-info`,
    });
  }
}

const service = new ProspectService();
export default service;
