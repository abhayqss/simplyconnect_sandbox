import { noop } from "underscore";

import { PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

import BaseService from "./BaseService";

const { FIRST_PAGE } = PAGINATION;

export class ClientService extends BaseService {
  find({ name, page = FIRST_PAGE, sort, size = 10, filter }, { getRequest = noop } = {}) {
    return super.request({
      url: "/clients",
      use: getRequest,
      params: { name, page: page - 1, sort, size, ...filter },
    });
  }

  findContactNoClient(params) {
    return super.request({
      url: "/authorized-directory/contacts",
      params,
    });
  }
  findSignatureContactNoClient(params) {
    return super.request({
      url: "/contacts",
      params,
    });
  }

  findById(clientId, options) {
    return super.request({
      url: `/clients/${clientId}`,
      mockParams: { id: clientId },
      ...options,
    });
  }

  // task 1
  // /clients/can-have-housing-vouchers?organizationId=123
  findCanHaveHousingVouchers(params) {
    return super.request({
      url: "/clients/can-have-housing-vouchers",
      params,
    });
  }

  canEditSSN(clientId) {
    return super.request({
      url: `/clients/${clientId}/can-edit-ssn`,
      response: { extractDataOnly: true },
    });
  }

  count(params) {
    return super.request({
      url: "/clients/count",
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
      url: "/clients",
      body: data,
      type: "multipart/form-data",
      headers,
    });
  }

  findRecords({ page = FIRST_PAGE, sort, size = 10, ...other }) {
    return super.request({
      url: "/clients/records",
      params: { page: page - 1, sort, size, ...other },
    });
  }

  findUnassociated(params) {
    return super.request({
      url: "/clients/unassociated",
      params,
    });
  }

  findEmergencyContacts(clientId) {
    return super.request({
      url: `/clients/${clientId}/emergency-contacts`,
    });
  }

  findNewContacts(clientId) {
    return super.request({
      url: `/clients/${clientId}/contacts`,
    });
  }

  findBillingDetails(clientId) {
    return super.request({
      url: `/clients/${clientId}/billing-info`,
    });
  }

  findPrimaryContacts({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/prospective-primary-contacts`,
    });
  }

  validateUniqInOrganization(data) {
    return super.request({
      url: "/clients/validate-uniq-in-organization",
      params: data,
    });
  }

  validateUniqInCommunity(data) {
    return super.request({
      url: "/clients/validate-uniq-in-community",
      params: data,
    });
  }

  canAdd(params) {
    return super.request({
      url: "/clients/can-add",
      params,
    });
  }

  canEdit({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/can-edit`,
    });
  }

  toggleStatus(clientId) {
    return super.request({
      url: `/clients/${clientId}/toggle-status`,
      method: "POST",
    });
  }

  activate({ clientId, ...data }) {
    return super.request({
      url: `/clients/${clientId}/activate`,
      method: "POST",
      body: data,
    });
  }

  deactivate({ clientId, ...data }) {
    return super.request({
      url: `/clients/${clientId}/deactivate`,
      method: "POST",
      body: data,
    });
  }

  saveEssentials(data) {
    return super.request({
      url: "/clients/edit-essentials",
      method: "POST",
      body: data,
      type: "multipart/form-data",
    });
  }

  findHouseholdMembers({ clientId, ...params }) {
    return super.request({
      url: `/clients/${clientId}/household-members`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findContact({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/telecom`,
      mockParams: { id: clientId },
      response: { extractDataOnly: true },
    });
  }

  validateUniqTcode(params) {
    return super.request({
      url: `/clients/validate-uniq-in-organization`,
      response: { extractDataOnly: true },
      params,
    });
  }
}

const service = new ClientService();
export default service;
