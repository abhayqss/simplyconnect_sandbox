import BaseService from "./BaseService";

export class MarketplaceService extends BaseService {
  buildingList({ page, size, name, organizationId }) {
    return super.request({
      url: `/communitybasic`,
      params: { page: page - 1, size, name, organizationId },
    });
  }

  buildingDetail(
    organizationId,
    communityId,
    isMarketplaceDataIncluded = true,
    options,
  ) {
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

  vendorType() {
    return super.request({
      url: `/vendor/type`,
    });
  }

  vendorList(params) {
    return super.request({
      url: "/vendor/find",
      params: params,
    });
  }

  vendorDetail(id) {
    return super.request({
      url: "/vendor",
      params: {
        id,
      },
    });
  }

  getVendorLogo(vendorId) {
    return super.request({
      url: `/vendor/${vendorId}/logo`,
    });
  }

  getVendorPhoto(photoId) {
    return super.request({
      url: `/vendor/photos/${photoId}`,
    });
  }

  getBuildingLogo(communityId) {
    return super.request({
      url: `/communitybasic/${communityId}/logo`,
    });
  }

  getBuildingPhoto(organizationId, communityId, pictureId) {
    return super.request({
      url: `/organizations/${organizationId}/communities/${communityId}/pictures/${pictureId}`,
    });
  }

  getStateOptions(params, options) {
    return super.request({
      url: "/directory/states",
      ...options,
      params,
    });
  }

  findVendorCategoryType(params, options) {
    return super.request({
      url: `/vendor/type`,
      ...options,
      params,
    });
  }

  findAllVendor(params) {
    return super.request({
      url: "/vendor/findAll",
      params,
    });
  }

  findVendorReferOrganizations(params, options) {
    return super.request({
      url: "/authorized-directory/organizations",
      ...options,
      params,
    });
  }

  findVendorReferCommunities(params, options) {
    return super.request({
      url: "/authorized-directory/communities",
      ...options,
      params,
    });
  }

  findVendorReferCommunityDetail(params, options) {
    return super.request({
      url: `/communitybasic/${params}`,
      ...options,
    });
  }

  findAllCareTeamMembers(params) {
    return super.request({
      // url: `/care-team-members/findall`,
      url: `/care-team-members`,
      params,
    });
  }

  /**
   * 获取所有clients
   * @function findAllClients
   * @param {Object} params - 搜索客户所需的参数。
   * @param {String} params.communityIds - 必须包含的社区ID数组。
   * @param {String} params.organizationId - 必须包含的组织ID。
   */
  findAllClients(params) {
    return super.request({
      url: `/clients`,
      params,
    });
  }

  saveVendorRefer(body) {
    return super.request({
      url: `/vendorRefer/save`,
      method: "POST",
      body,
    });
  }

  /**
   *
   * @param params
   * @param {number} [params.page =0] - 分页
   * @param {number} [params.size =4] - 个数
   * @param {string} [params.sort='referTime,desc'] - 排序
   * @return {Promise<unknown>|*}
   */
  getVendorReferHistory(params) {
    return super.request({
      url: `/vendorRefer`,
      params,
    });
  }

  /**
   *
   * @param params
   * @param params.name
   * @param params.page
   * @param params.size
   * @return {Promise<unknown>|*}
   */
  getPublicBuildingList(params) {
    return super.request({
      url: `/communitybasic/findAllCommunity`,
      params,
    });
  }

  //   Send Inquiry
  sendInquiry(body) {
    return super.request({
      url: `/communitybasic/sendInquiry`,
      method: "POST",
      body,
    });
  }

  findSendInquiry(buildingId) {
    return super.request({
      url: `/communitybasic/${buildingId}/findSendInquiry`,
    });
  }
}

const service = new MarketplaceService();
export default service;
