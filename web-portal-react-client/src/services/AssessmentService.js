import BaseService from "./BaseService";
import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

const { FIRST_PAGE } = PAGINATION;

const { TXT, XLSX } = ALLOWED_FILE_FORMATS;

export class AssessmentService extends BaseService {
  find({ name, clientId, page = FIRST_PAGE, size = 10, sort }) {
    return super.request({
      url: `/clients/${clientId}/assessments`,
      params: { name, page: page - 1, size, sort },
    });
  }

  findById(clientId, assessmentId) {
    return super.request({
      url: `/clients/${clientId}/assessments/${assessmentId}`,
    });
  }

  findDefault({ clientId, ...params }) {
    return super.request({
      url: `/clients/${clientId}/assessments/default`,
      params,
    });
  }

  findHistory({ name, clientId, assessmentId, page = FIRST_PAGE, size = 10 }) {
    return super.request({
      url: `/clients/${clientId}/assessments/${assessmentId}/history`,
      params: { name, page: page - 1, size },
    });
  }

  isAnyInProcess({ clientId, ...params }) {
    return super.request({
      url: `/clients/${clientId}/assessments/any-in-process`,
      params,
    });
  }

  count({ clientId }, options) {
    return super.request({
      url: `/clients/${clientId}/assessments/count`,
      ...options,
    });
  }

  statistics({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/assessments/statistics`,
    });
  }

  save(clientId, { isAutoSave, ...assessment }) {
    const isNew = isEmpty(assessment.id);

    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";

    const headers = !isAutoSave
      ? {}
      : {
          "X-Auth-With-Cookies": "no-update",
          // Authorization: AuthorizationData,
        };

    return super.request({
      method: isNew ? "PUT" : "POST",
      url: `/clients/${clientId}/assessments`,
      body: assessment,
      type: "json",
      headers,
    });
  }

  saveServicePlanNeedIdentification(data, { clientId, assessmentId }) {
    return super.request({
      method: "PUT",
      url: `/clients/${clientId}/assessments/${assessmentId}/service-plan-need-identification`,
      body: data,
      type: "json",
    });
  }

  canView({ clientId }, options = {}) {
    return super.request({
      url: `/clients/${clientId}/assessments/can-view`,
      ...options,
    });
  }

  canAdd({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/assessments/can-add`,
    });
  }

  download(clientId, assessmentId) {
    return super.request({
      type: ALLOWED_FILE_FORMAT_MIME_TYPES[TXT],
      url: `/clients/${clientId}/assessments/${assessmentId}/export`,
    });
  }

  hideById(assessmentId, { comment, clientId }) {
    return super.request({
      method: "PUT",
      url: `/clients/${clientId}/assessments/${assessmentId}/hide`,
      body: { comment },
      type: "json",
    });
  }

  restoreById(assessmentId, { comment, clientId }) {
    return super.request({
      method: "PUT",
      url: `/clients/${clientId}/assessments/${assessmentId}/restore`,
      body: { comment },
      type: "json",
    });
  }

  canGenerateInTuneReport({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/assessments/in-tune-report/can-generate`,
      response: { extractDataOnly: true },
    });
  }

  canDownloadInTuneReport({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/assessments/in-tune-report/can-download`,
      response: { extractDataOnly: true },
    });
  }

  downloadInTuneReport({ clientId }) {
    return super.request({
      type: ALLOWED_FILE_FORMAT_MIME_TYPES[XLSX],
      url: `/clients/${clientId}/assessments/in-tune-report/download`,
    });
  }
}

const service = new AssessmentService();
export default service;
