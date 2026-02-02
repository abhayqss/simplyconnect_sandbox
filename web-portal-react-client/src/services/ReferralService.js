import { noop } from "underscore";

import { getUrl } from "lib/utils/UrlUtils";
import { isEmpty, isInteger } from "lib/utils/Utils";

import BaseService from "./BaseService";

import { PAGINATION } from "lib/Constants";

const { FIRST_PAGE } = PAGINATION;

export class ReferralService extends BaseService {
  find({ page = FIRST_PAGE, ...other }, { getRequest = noop } = {}) {
    return super.request({
      use: getRequest,
      url: "/referrals",
      params: { page: page - 1, ...other },
    });
  }

  findById(referralId, params) {
    return super.request({
      url: `/referrals/${referralId}`,
      params,
    });
  }

  canAdd(params) {
    return super.request({
      url: "/referrals/can-add",
      params,
    });
  }

  save(data) {
    const isNew = isEmpty(data.id);

    return super.request({
      method: isNew ? "POST" : "PUT",
      url: "/referrals",
      body: data,
      type: "multipart/form-data",
    });
  }

  saveVendor(data) {
    const isNew = isEmpty(data.id);

    return super.request({
      method: isNew ? "POST" : "PUT",
      url: "/referrals/vendor",
      body: data,
      type: "multipart/form-data",
    });
  }

  count(params) {
    return super.request({
      url: "referrals/count",
      params,
    });
  }

  findRecipients(params) {
    return super.request({
      url: "/referrals/recipients",
      params,
    });
  }

  findRequests({ referralId, page = FIRST_PAGE, ...other }) {
    return super.request({
      url: getUrl({
        resources: [{ name: "referrals", id: referralId }, "referral-requests"],
      }),
      params: { page: page - 1, ...other },
    });
  }

  findRequestContacts({ requestId }) {
    return super.request({
      url: `/referrals/referral-requests/${requestId}/contacts`,
    });
  }

  findRequestSenders(params) {
    return super.request({
      url: "/referrals/referral-requests/senders",
      params,
    });
  }

  findRequestById(requestId, { referralId, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "referrals", id: referralId, hasId: isInteger(referralId) },
          { name: "referral-requests", id: requestId },
        ],
      }),
      params: other,
    });
  }

  findInfoRequests({ referralId, requestId, page = FIRST_PAGE, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "referrals", id: referralId, hasId: isInteger(referralId) },
          { name: "referral-requests", id: requestId },
          "info-requests",
        ],
      }),
      params: { page: page - 1, ...other },
    });
  }

  findInfoRequestById({ requestId, referralId, infoRequestId, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "referrals", id: referralId, hasId: isInteger(referralId) },
          { name: "referral-requests", id: requestId },
          { name: "info-requests", id: infoRequestId },
        ],
      }),
      params: other,
    });
  }

  sendInfoRequest(data, { requestId }) {
    return super.request({
      method: "POST",
      url: getUrl({
        resources: ["referrals", { name: "referral-requests", id: requestId }, "info-requests"],
      }),
      body: data,
      type: "json",
    });
  }

  sendInfoResponse(data, { referralId, infoRequestId }) {
    return super.request({
      method: "PUT",
      url: getUrl({
        resources: [{ name: "referrals", id: referralId }, { name: "info-requests", id: infoRequestId }, "respond"],
      }),
      body: data,
      type: "json",
    });
  }

  findDefault(params) {
    return super.request({
      url: "/referrals/default",
      response: { extractDataOnly: true },
      params,
    });
  }

  assignToRequest(contactId, { requestId }) {
    return super.request({
      method: "PUT",
      url: `/referrals/referral-requests/${requestId}/contacts/${contactId}/assign`,
    });
  }

  unassignFromRequest({ requestId }) {
    return super.request({
      method: "PUT",
      url: `/referrals/referral-requests/${requestId}/contacts/unassign`,
    });
  }

  declineRequest(data, requestId) {
    return super.request({
      method: "POST",
      url: `/referrals/referral-requests/${requestId}/decline`,
      body: data,
    });
  }

  acceptRequest(requestId, data) {
    return super.request({
      method: "POST",
      url: `/referrals/referral-requests/${requestId}/accept`,
      body: data,
    });
  }

  preadmitRequest(requestId) {
    return super.request({
      method: "POST",
      url: `/referrals/referral-requests/${requestId}/preadmit`,
    });
  }

  cancelRequest(requestId) {
    return super.request({
      method: "POST",
      url: `/referrals/${requestId}/cancel`,
    });
  }

  findClientCommunities(params) {
    return super.request({
      url: `/authorized-directory/communities`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findClientOrganizations(params) {
    return super.request({
      response: { extractDataOnly: true },
      url: `/authorized-directory/organizations`,
      params,
    });
  }

  /*  findClientCommunities(params) {
    return super.request({
      url: `/referrals/communities`,
      response: { extractDataOnly: true },
      params,
    });
  }*/
  /* findClientOrganizations(params) {
    return super.request({
      response: { extractDataOnly: true },
      url: `/referrals/organizations`,
      params,
    });
  }*/
}

export default new ReferralService();
