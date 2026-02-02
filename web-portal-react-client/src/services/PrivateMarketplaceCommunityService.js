import BaseService from './BaseService'
import { PAGINATION } from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class PrivateMarketplaceCommunityService extends BaseService {
    find ({ communityId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/marketplace/communities${communityId ? `/${communityId}/partners`: ''}`,
            params: { page: page - 1, size, ...other }
        })
    }

    findById (communityId, params) {
        return super.request({
            url: `/marketplace/communities/${communityId}`,
            mockParams: { communityId },
            response: { extractDataOnly: true },
            params
        })
    }

    count () {
        return super.request({
            url: `/marketplace/communities/count`
        })
    }

    appointment (communityId, data) {
        return super.request({
            method: 'POST',
            url: `/marketplace/communities/${communityId}/appointment`,
            body: data,
            type: 'json'
        })
    }

    canView () {
        return super.request({
            url: '/marketplace/communities/can-view'
        })
    }

    canEditPartnerProviders({ communityId, organizationId, ...other }) {
        return super.request({
            url: `/marketplace/communities/can-edit-partner-providers`,
            params: { communityId, organizationId, ...other }
        })
    }

    findSaved(params) {
        return super.request({
            url: '/marketplace/communities/saved',
            response: { extractDataOnly: true },
            params
        })
    }

    saveById(communityId) {
        return super.request({
            method: 'POST',
            type: 'json',
            url: `/marketplace/communities/${communityId}/save`,
            response: { extractDataOnly: true },
        })
    }

    removeById(communityId) {
        return super.request({
            method: 'POST',
            type: 'json',
            url: `/marketplace/communities/${communityId}/remove`,
            response: { extractDataOnly: true },
        })
    }

    findLocations(params) {
        return super.request({
            url: '/marketplace/communities/locations',
            response: { extractDataOnly: true },
            params
        })
    }

    findLocationById(communityId, params) {
        return super.request({
            url: `/marketplace/communities/${communityId}/location-details`,
            response: { extractDataOnly: true },
            params
        })
    }

    inNetworkCommunityExists(params) {
        return super.request({
            url: `/marketplace/communities/in-network/exists`,
            response: { extractDataOnly: true },
            params
        })
    }

    findProviders({
        communityId,
        page = FIRST_PAGE,
        size = 10,
        ...other
    }) {
        return super.request({
            url: `/marketplace/communities/${communityId}/providers`,
            params: { 
                page: page - 1,
                size, 
                ...other
            }
        })
    }
}

const service = new PrivateMarketplaceCommunityService()
export default service