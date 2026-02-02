import BaseService from "./BaseService";

export class QrCodeService extends BaseService {
  featCategories(organizationId) {
    return super.request({
      url: `/organizationbasics/categories`,
      params: { organizationId },
    });
  }

  featCommunities(organizationId) {
    return super.request({
      url: `/communitybasic/findByOrgId`,
      params: {
        organizationId,
      },
    });
  }

  associatedOrg(vendorId, referIds) {
    return super.request({
      url: `/vendor/admin/addAssociateOrg`,
      method: "POST",
      body: {
        vendorId,
        referIds: [referIds],
      },
    });
  }

  featBuildingQrCode(communityId) {
    return super.request({
      url: `/communitybasic/generateCode/${communityId}`,
    });
  }

  featQrBuildingDetail(communityId) {
    return super.request({
      url: `/communitybasic/${communityId}`,
    });
  }

  associatedBuilding(vendorId, referIds) {
    return super.request({
      url: `/vendor/admin/addAssociateCommunity`,
      method: "POST",
      body: {
        vendorId,
        referIds: [referIds],
      },
    });
  }

  downOrgQrCode(communityId) {
    return super.request({
      url: `/communitybasic/downloadCode/${communityId}`,
    });
  }

  qrVendorCreate(data) {
    return super.request({
      url: `/vendor/qr/create`,
      method: "POST",
      body: data,
      type: "multipart/form-data",
    });
  }

  featOrgCommunitiesQr(orgID) {
    return super.request({
      url: `/communitybasic/downloadAllCode/${orgID}`,
    });
  }

  featServicesByCategory(categoryIds) {
    return super.request({
      url: `/vendor/type/services`,
      params: { categoryIds },
    });
  }
}

const service = new QrCodeService();
export default service;
