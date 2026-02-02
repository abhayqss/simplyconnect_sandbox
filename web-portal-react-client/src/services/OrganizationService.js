import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

const { FIRST_PAGE } = PAGINATION;

export class OrganizationService extends BaseService {
  find({ name, sort, page = FIRST_PAGE, size = 10 }) {
    return super.request({
      url: `/organizations`,
      params: { name, sort, page: page - 1, size },
    });
  }

  findById(orgId, { isMarketplaceDataIncluded = false } = {}, options) {
    return super.request({
      url: `/organizations/${orgId}`,
      ...options,
      params: {
        marketplaceDataIncluded: isMarketplaceDataIncluded,
      },
      mockParams: {
        id: orgId,
        marketplaceDataIncluded: isMarketplaceDataIncluded,
      },
    });
  }

  downloadLogoById(id) {
    return super.request({
      url: `/organizations/${id}/logo`,
    });
  }

  findPermissions(params) {
    return super.request({
      url: `/organizations/permissions`,
      response: { extractDataOnly: true },
      params,
    });
  }

  count() {
    return super.request({
      url: `/organizations/count`,
    });
  }

  save(organization) {
    const isNew = isEmpty(organization.id);

    return super.request({
      method: isNew ? "POST" : "PUT",
      /*            url: `/organizations`,*/
      url: `/organizations`,
      body: organization,
      type: "multipart/form-data",
    });
  }

  deleteOrganization(organizationId) {
    return super.request({
      method: "DELETE",
      url: `/organizations/${organizationId}/remove`,
    });
  }

  canView({ organizationId, ...params }) {
    return super.request({
      url: `/organizations/${organizationId}/can-view`,
      response: { extractDataOnly: true },
      params,
    });
  }

  canAdd() {
    return super.request({
      url: "/organizations/can-add",
    });
  }

  validateUniq(data) {
    return super.request({
      url: "/organizations/validate-uniq",
      params: data,
    });
  }

  featQrCode(organizationId) {
    return super.request({
      url: `/organizationbasics/generateCode/${organizationId}`,
    });
  }

  featOrgDetail(organizationId) {
    return super.request({
      url: `/organizationbasics/${organizationId}`,
    });
  }

  getQrCodePic(communityId, pictureId) {
    return super.request({
      url: `/communitybasic/${communityId}/pictures/${pictureId}`,
    });
  }

  // community detail vendors
  featVendorOfAssociation(params) {
    return super.request({
      url: `/communitybasic/findCommunityVendor`,
      params,
    });
  }

  // community detail vendor marketplace
  changeStatusOfMarketplace(body) {
    return super.request({
      method: "POST",
      url: `/communitybasic/modifyPrimaryType`,
      // type: "multipart/form-data",
      body,
    });
  }
}

const service = new OrganizationService();
export default service;
