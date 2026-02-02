import BaseService from './BaseService'
import { PAGINATION } from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class PublicMarketplaceCommunityService extends BaseService {
    find({ page = FIRST_PAGE, size = 10, ...other }, options) {
        return super.request({
            url: `/open-marketplace/service-providers`,
            params: { page: page - 1, size, ...other },
            ...options
        })
    }

    findById (providerId, params) {
        return super.request({
            url: `/open-marketplace/service-providers/${providerId}`,
            response: { extractDataOnly: true },
            params
        })
    }

    count () {
        return super.request({
            url: `/open-marketplace/service-providers/count`
        })
    }

    findOrganizationByCode (organizationCode, params) {
        return super.request({
            url: `/open-marketplace/organizations/${organizationCode}`,
            response: { extractDataOnly: true },
            params
        })
    }

    downloadLogo ({ providerId, ...params }) {
        return super.request({
            url: `/open-marketplace/service-providers/${providerId}/logo`,
            response: { extractDataOnly: true },
            params
        })
    }

    downloadOrganizationLogo ({ organizationCode, ...params }) {
        return super.request({
            url: `/open-marketplace/organizations/${organizationCode}/logo`,
            response: { extractDataOnly: true },
            params
        })
    }

    downloadPictureById(pictureId, { providerId, ...params }) {
        return super.request({
            url: `/open-marketplace/service-providers/${providerId}/pictures/${pictureId}`,
            response: { extractDataOnly: true },
            params
        })
    }

    saveInquiry(data) {
        return super.request({
            method: 'POST',
            url: `/open-marketplace/inquiries`,
            body: data,
            type: 'application/json',
            responseTimeout: 1200000,
            response: { extractDataOnly: true }
        })
    }
}

const service = new PublicMarketplaceCommunityService()
export default service