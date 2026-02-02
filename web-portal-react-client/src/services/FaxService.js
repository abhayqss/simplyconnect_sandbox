import BaseService from "./BaseService";
import { noop } from "underscore";
import { DateUtils } from "lib/utils/Utils";
import { PAGINATION } from "lib/Constants";

const { format, formats } = DateUtils;

const { FIRST_PAGE } = PAGINATION;

export class FaxService extends BaseService {
  SentFaxList(params) {
    return super.request({
      url: `/fax`,
      params,
    });
  }

  ReceivedFaxList(params) {
    return super.request({
      url: `/fax/findInboundList`,
      params,
    });
  }

  AddSentFax(params) {
    return super.request({
      url: `/fax/send`,
      method: "POST",
      body: params,
      type: "multipart/form-data",
    });
  }

  findContact({ name, page = FIRST_PAGE, sort, size = 10, ...other }, { getRequest = noop } = {}) {
    return super.request({
      url: `/contacts`,
      use: getRequest,
      params: { name, page: page - 1, size, sort, ...other },
    });
  }
  findVendorMember({ name, page = FIRST_PAGE, sort, size = 10, ...other }, { getRequest = noop } = {}) {
    return super.request({
      url: `/contacts/vendorMember`,
      use: getRequest,
      params: { name, page: page - 1, size, sort, ...other },
    });
  }

  findOneId(id) {
    return super.request({
      url: `/contacts/${id}`,
    });
  }

  FeatAssociationDetail(id) {
    return super.request({
      url: `/association?id=${id}`,
    });
  }

  //downloadPdf
  DownloadFax(params) {
    return super.request({
      url: `/fax/downloadPdf`,
      // type: ALLOWED_FILE_FORMAT_MIME_TYPES[format],
      params,
    });
  }

  //Delete
  DeleteFax(params) {
    return super.request({
      method: "DELETE",
      url: `/fax`,
      params,
    });
  }

  RefreshCloudFax(params) {
    return super.request({
      method: "GET",
      url: `/fax/synchrodata`,
      params,
    });
  }

  JudgeCanFax(params) {
    return super.request({
      method: "GET",
      url: `/fax/canFax`,
      params,
    });
  }
}

const adminFaxService = new FaxService();
export default adminFaxService;
