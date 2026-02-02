import { isEmpty } from "lib/utils/Utils";

import BaseService from "./BaseService";

export class DocumentESignService extends BaseService {
  findTemplateById({ templateId, ...params }) {
    return super.request({
      url: `/documents/e-sign/templates/${templateId}`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findTemplates(params) {
    return super.request({
      url: `/documents/e-sign/templates`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findTemplatesTree(params) {
    return super.request({
      url: `/documents/e-sign/templates-tree`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findMultipleESignRequestedDocuments(params) {
    return super.request({
      url: `/documents/e-sign/multiTemplates`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findMultipleESignRequestedDocumentsDetail(templateId) {
    return super.request({
      url: `/documents/e-sign/templates/${templateId}`,
      response: { extractDataOnly: true },
    });
  }

  // Use this method to facilitate multiple selection of data
  findTemplateSchemeById({ templateId, ...params }) {
    return super.request({
      url: `/documents/e-sign/templates/${templateId}`,
      response: { extractDataOnly: true },
      params,
    });
  }

  downloadTemplatePreviewFile({ templateId, ...templateData }) {
    return super.request({
      method: "POST",
      url: `/documents/e-sign/templates/${templateId}/preview`,
      response: { extractDataOnly: true },
      body: templateData,
    });
  }

  findContacts(params) {
    return super.request({
      url: `/documents/e-sign/contacts`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findContactDetail(id) {
    return super.request({
      url: `/contacts/${id}`,
      response: { extractDataOnly: true },
    });
  }

  findDefaultAssignedTemplateFolders(params) {
    return super.request({
      url: `/documents/e-sign/default-template-folders`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findAllOrganizations() {
    return super.request({
      url: `/authorized-directory/organizations`,
      response: { extractDataOnly: true },
    });
  }

  findOrganizations(params) {
    return super.request({
      url: `/documents/e-sign/organizations`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findCommunities(params) {
    return super.request({
      url: `/documents/e-sign/communities`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findBulkRequests({ bulkRequestId, ...params }) {
    return super.request({
      url: `/documents/e-sign/bulk-requests/${bulkRequestId}/requests`,
      response: { extractDataOnly: true },
      params,
    });
  }

  canAddSignature(params) {
    return super.request({
      url: `/documents/e-sign/requests/can-add`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findSignatureRequestCount(params) {
    return super.request({
      url: `/documents/e-sign/requests/count`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findTemplateCount(params) {
    return super.request({
      url: `/documents/e-sign/templates/count`,
      response: { extractDataOnly: true },
      params,
    });
  }

  canAddTemplate(params) {
    return super.request({
      url: `/documents/e-sign/templates/can-add`,
      response: { extractDataOnly: true },
      params,
    });
  }

  saveTemplate(data, options = {}) {
    const isNew = isEmpty(data.id);
    const url = isNew ? `/documents/e-sign/templates` : `/documents/e-sign/templates/${data.id}`;

    return super.request({
      method: isNew ? "POST" : "PUT",
      url,
      body: data,
      type: "multipart/form-data",
      ...options,
    });
  }

  autoSaveTemplate(data) {
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";

    return this.saveTemplate(data, {
      headers: {
        // Authorization: AuthorizationData,
        "X-Auth-With-Cookies": "no-update",
      },
    });
  }

  deleteTemplateById(templateId, params) {
    return super.request({
      method: "DELETE",
      url: `/documents/e-sign/templates/${templateId}`,
      params,
    });
  }

  findSignatureHistory({ page, ...other }) {
    return super.request({
      url: `/documents/e-sign/history`,
      response: { extractDataOnly: true },
      params: { page: page - 1, ...other },
    });
  }

  findSignatureRequestById(id) {
    return super.request({
      url: `/documents/e-sign/requests/${id}`,
      response: { extractDataOnly: true },
    });
  }

  findESignRequestedDocuments(params) {
    return super.request({
      url: `/documents/e-sign-requested`,
      response: { extractDataOnly: true },
      params,
    });
  }

  submitSignatureRequest({ isMultipleSignature, ...data }) {
    const url = isMultipleSignature ? `/documents/e-sign/bulk-requests` : `/documents/e-sign/requests`;

    return super.request({
      method: "POST",
      url,
      response: { extractDataOnly: true },
      body: data,
    });
  }

  cancelSignatureRequestById(id) {
    return super.request({
      method: "POST",
      url: `/documents/e-sign/requests/${id}/cancel`,
      response: { extractDataOnly: true },
    });
  }

  renewSignatureRequestById(id, params) {
    return super.request({
      method: "POST",
      url: `/documents/e-sign/requests/${id}/renew`,
      response: { extractDataOnly: true },
      body: params,
    });
  }

  cancelBulkRequestById(id, params) {
    return super.request({
      method: "POST",
      url: `/documents/e-sign/bulk-requests/${id}/cancel?templateId=${params.templateId}`,
      response: { extractDataOnly: true },
    });
  }

  renewBulkRequestById(id, params) {
    return super.request({
      method: "POST",
      url: `/documents/e-sign/bulk-requests/${id}/renew`,
      response: { extractDataOnly: true },
      body: params,
    });
  }

  resendSignatureRequestPin({ requestId, ...params }) {
    return super.request({
      method: "POST",
      url: `/documents/e-sign/requests/${requestId}/resend-pin`,
      response: { extractDataOnly: true },
      body: params,
    });
  }

  sendMultiple(params) {
    return super.request({
      method: "POST",
      url: `/documents/e-sign/bulk-requests/multi`,
      response: { extractDataOnly: true },
      body: params,
    });
  }
}

const service = new DocumentESignService();
export default service;
