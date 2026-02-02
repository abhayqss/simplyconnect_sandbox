import { BaseService } from "services";

import { isEmpty } from "lib/utils/Utils";
import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS } from "../lib/Constants";

const { ZIP } = ALLOWED_FILE_FORMATS;

class DocumentFolderService extends BaseService {
  find(params) {
    return super.request({
      url: `/document-folders`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findById(folderId, params) {
    return super.request({
      url: `/document-folders/${folderId}`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findDefault(params) {
    return super.request({
      url: `/document-folders/default`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findPermissions(params) {
    return super.request({
      url: `/document-folders/permissions`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findContacts(params) {
    return super.request({
      url: `/document-folders/contacts`,
      response: { extractDataOnly: true },
      params,
    });
  }

  downloadById(folderId) {
    return super.request({
      url: `/document-folders/${folderId}/download`,
      type: ALLOWED_FILE_FORMAT_MIME_TYPES[ZIP],
    });
  }

  canAdd(params) {
    return super.request({
      url: `/document-folders/can-add`,
      response: { extractDataOnly: true },
      params,
    });
  }

  canView(params) {
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";
    return super.request({
      url: `/document-folders/can-view`,
      response: { extractDataOnly: true },
      params,
      headers: {
        // Authorization: AuthorizationData,
        "X-Auth-With-Cookies": "no-update",
      },
    });
  }

  validateUniqName(params) {
    return super.request({
      url: `/document-folders/validate-uniq`,
      response: { extractDataOnly: true },
      params,
    });
  }

  save(data) {
    const isNew = isEmpty(data.id);

    const url = isNew ? `/document-folders` : `/document-folders/${data.id}`;

    return super.request({
      url,
      method: isNew ? "POST" : "PUT",
      type: "multipart/form-data",
      body: data,
    });
  }

  deleteById(folderId, params) {
    return super.request({
      method: "DELETE",
      url: `/document-folders/${folderId}`,
      params,
    });
  }

  restoreById(folderId) {
    return super.request({
      method: "POST",
      url: `/document-folders/${folderId}/restore`,
    });
  }
}

export default new DocumentFolderService();
