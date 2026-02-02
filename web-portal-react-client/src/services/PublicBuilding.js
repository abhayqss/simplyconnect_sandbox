import BaseService from './BaseService';

export class PublicBuildingService extends BaseService {

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
      params
    })
  }

  getBuildingLogo(communityId) {
    return super.request({
      url: `/communitybasic/${communityId}/logo`,
    })
  }

  featQrBuildingDetail(communityId) {
    return super.request({
      url: `/communitybasic/${communityId}`,
    })
  }

  getQrCodePic(communityId,pictureId) {
    return super.request({
      url: `/communitybasic/${communityId}/pictures/${pictureId}`,
    })
  }


}


const service = new PublicBuildingService()
export default service
