import { noop } from 'underscore'

import { PAGINATION } from 'lib/Constants'
import { isEmpty } from 'lib/utils/Utils'

import BaseService from './BaseService'

const { FIRST_PAGE } = PAGINATION

export class ClientAccessRequestService extends BaseService {
    find(
        { clientId, page = FIRST_PAGE, size = 15, ...other },
        { getRequest = noop } = {}
    ) {
        return super.request({
            url: `/clients/${clientId}/access-requests`,
            use: getRequest,
            params: { page: page - 1, size, ...other }
        })
    }

    save({ clientId } = {}) {
        return super.request({
            method: 'POST',
            url: `/clients/${clientId}/access-requests`,
            type: 'multipart/form-data',
            response: { extractDataOnly: true }
        })
    }

    findById(requestId, { clientId }) {
        return super.request({
            url: `/clients/${clientId}/access-requests/${requestId}`,
            response: { extractDataOnly: true }
        })
    }

    approve({ clientId, requestId } = {}) {
        return super.request({
            method: 'POST',
            url: `/clients/${clientId}/access-requests/${requestId}/accept`,
            type: 'multipart/form-data',
            response: { extractDataOnly: true }
        })
    }

    decline({ clientId, requestId } = {}) {
        return super.request({
            method: 'POST',
            url: `/clients/${clientId}/access-requests/${requestId}/decline`,
            type: 'multipart/form-data',
            response: { extractDataOnly: true }
        })
    }
}

const service = new ClientAccessRequestService()
export default service