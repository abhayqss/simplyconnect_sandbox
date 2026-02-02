import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

const { FIRST_PAGE } = PAGINATION;

export class CommunityService extends BaseService {
  find({ orgId, name, sort, page = FIRST_PAGE, size = 10 }) {
    return super.request({
      url: `/organizations/${orgId}/communities`,
      mockParams: { orgId },
      params: { name, sort, page: page - 1, size },
    });
  }

  findById(communityId, { organizationId, isMarketplaceDataIncluded = false }, options) {
    return super.request({
      url: `/organizations/${organizationId}/communities/${communityId}`,
      params: {
        marketplaceDataIncluded: isMarketplaceDataIncluded,
      },
      mockParams: {
        id: communityId,
        orgId: organizationId,
        marketplaceDataIncluded: isMarketplaceDataIncluded,
      },
      ...options,
    });
  }

  downloadLogo(orgId, commId) {
    return super.request({
      url: `/organizations/${orgId}/communities/${commId}/logo`,
    });
  }

  downloadLogoById(id, { organizationId }) {
    return super.request({
      url: `/organizations/${organizationId}/communities/${id}/logo`,
    });
  }

  findPermissions({ organizationId, ...params }) {
    return super.request({
      url: `/organizations/${organizationId}/communities/permissions`,
      response: { extractDataOnly: true },
      params,
    });
  }

  count(orgId) {
    return super.request({
      url: `/organizations/${orgId}/communities/count`,
    });
  }

  save(data, { organizationId }) {
    return super.request({
      method: isEmpty(data.id) ? "POST" : "PUT",
      url: `/organizations/${organizationId}/communities`,
      body: data,
      type: "multipart/form-data",
    });
  }

  canAdd(orgId) {
    return super.request({
      url: `/organizations/${orgId}/communities/can-add`,
    });
  }

  canConfigure(params, options) {
    return super.request({
      url: `/docutrack/can-configure`,
      params,
      ...options,
    });
  }

  validateUniq(orgId, data) {
    return super.request({
      url: `/organizations/${orgId}/communities/validate-uniq`,
      params: data,
    });
  }

  findTreatmentServices({ organizationId, communityId }) {
    return super.request({
      url: `/organizations/${organizationId}/communities/${communityId}/services-treatment-approaches`,
      response: { extractDataOnly: true },
    });
  }

  findServices({ organizationId, communityId }) {
    return super.request({
      url: `/organizations/${organizationId}/communities/${communityId}/services`,
      response: { extractDataOnly: true },
    });
  }
  findVendorServices({ vendorId }) {
    return super.request({
      url: `/vendor/admin/${vendorId}/services`,
      response: { extractDataOnly: true },
    });
  }

  /*  findVendorServices(organizationId) {
    return super.request({
      url: `/organizations/${organizationId}/communities/services`,
      response: { extractDataOnly: true },
    });
  }*/

  findPictureById(id, { organizationId, communityId }) {
    return super.request({
      url: `/organizations/${organizationId}/communities/${communityId}/pictures/${id}`,
    });
  }

  loadServerSelfSignedCertificate(params) {
    return super.request({
      url: `/docutrack/load-server-self-signed-cert`,
      params,
    });
  }

  getNonUniqBusinessUnitCodes(params) {
    return super.request({
      url: `/docutrack/non-unique-business-unit-codes`,
      params,
    });
  }
}

const service = new CommunityService();
export default service;
