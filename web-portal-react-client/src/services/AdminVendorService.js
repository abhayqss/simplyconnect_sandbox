import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

const { FIRST_PAGE } = PAGINATION;

// admin vendor
export class AdminVendorService extends BaseService {
  // findVendorList
  findVendorList(params) {
    return super.request({
      url: `/vendor/admin/find`,
      params,
    });
  }

  getVendorContactData(contactId) {
    return super.request({
      url: `/vendor/admin/contact/${contactId} `,
    });
  }

  // add and edit
  saveVendorForm(vendor) {
    const isNew = isEmpty(vendor.id);
    return super.request({
      method: isNew ? "POST" : "PUT",
      url: `/vendor/admin`,
      body: vendor,
      type: "multipart/form-data",
    });
  }

  // find vendor  companyType
  findVendorCompanyType(params, options) {
    return super.request({
      url: `/vendor/type/companyType`,
      ...options,
      params,
    });
  }

  // find vendor  Category
  findVendorCategoryType(params, options) {
    return super.request({
      url: `/vendor/type`,
      ...options,
      params,
    });
  }

  findById(vendorId, { isMarketplaceDataIncluded = false } = {}, options) {
    return super.request({
      url: `/vendor/admin`,
      ...options,
      params: {
        id: vendorId,
      },
    });
  }

  viewVendorPhotos(photoId) {
    return super.request({
      url: `/vendor/photos/${photoId}`,
    });
  }

  // view detail bottom table
  viewVendorDetailCommunities(params) {
    return super.request({
      url: "/vendor/admin/findCommunity",
      params,
    });
  }

  viewVendorDetailOrganizations(params) {
    return super.request({
      url: "/vendor/admin/findOrganization",
      params,
    });
  }

  viewVendorDetailReferHistory(params) {
    return super.request({
      url: "/vendor/admin/findReferHistory",
      params,
    });
  }

  // vendor detail bottom associate communities
  viewVendorAssociateCommunities(params) {
    return super.request({
      url: "/communitybasic/findLinkCommunity",
      params,
    });
  }

  // /vendor/admin/associateCommunity
  AddVendorAssociateCommunities(body) {
    return super.request({
      url: "/vendor/admin/associateCommunity",
      body,
      method: "POST",
    });
  }

  // /vendor/admin/disAssociateCommunity
  DisAddVendorAssociateCommunities(body) {
    return super.request({
      url: "/vendor/admin/disAssociateCommunity",
      body,
      method: "POST",
    });
  }

  // /associateOrg
  AddVendorAssociateOrganizations(body) {
    return super.request({
      url: "/vendor/admin/associateOrg",
      body,
      method: "POST",
    });
  }

  DisAddVendorAssociateOrganizations(body) {
    return super.request({
      url: "/vendor/admin/disAssociateOrg",
      body,
      method: "POST",
    });
  }

  saveContactData(body) {
    return super.request({
      url: `/vendor/admin/createContact`,
      body,
      method: "POST",
      type: "multipart/form-data",
    });
  }

  editContactData(body) {
    return super.request({
      url: `/vendor/admin/editContact`,
      body,
      method: "PUT",
      type: "multipart/form-data",
    });
  }

  // vendor contact table
  viewVendorContactData(params) {
    return super.request({
      url: `/vendor/admin/findContact`,
      params,
    });
  }

  // vendor contact item
  findVendorContactDetail(contactId) {
    return super.request({
      url: `/vendor/admin/findContact`,
      params: {
        contactId,
      },
    });
  }

  // organizations的接口
  viewVendorAssociateOrganizations(params) {
    return super.request({
      url: "/organizationbasics",
      params,
    });
  }

  judgeVendorPending(body) {
    return super.request({
      url: "/vendor/admin/approve",
      body,
      method: "POST",
    });
  }

  judgeVendorEmail(body) {
    return super.request({
      url: "/vendor/admin/checkLoginName",
      body,
      method: "POST",
      type: "multipart/form-data",
    });
  }

  findCareTeamRoles({ contactId, nonClinicalTeam }) {
    return super.request({
      url: "/authorized-directory/care-team/client-member-roles",
      params: { contactId, nonClinicalTeam },
    });
  }

  getVendorServicesList({ categoryIds }) {
    return super.request({
      url: "/vendor/type/services",
      params: { categoryIds },
    });
  }
  canAddVendor() {
    return super.request({
      url: `/vendor/admin/canAdd`,
    });
  }

  fetchUnassociatedVendorList() {
    return super.request({
      url: "/",
    });
  }
  linkVendors(params) {
    return super.request({
      url: `/organizations/${params.organizationId}/communities/associateVendor`,
      method: "POST",
      body: {
        communityId: params.communityId,
        referIds: params.referIds,
      },
    });
  }
}

const adminVendorService = new AdminVendorService();
export default adminVendorService;
