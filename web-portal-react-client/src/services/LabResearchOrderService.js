import { noop } from 'underscore'

import { isEmpty } from 'lib/utils/Utils'
import { getUrl } from 'lib/utils/UrlUtils'

import BaseService from './BaseService'

import {
    PAGINATION
} from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class LabResearchOrderService extends BaseService {
    find(
        { page = FIRST_PAGE, ...other },
        { getRequest = noop } = {}
    ) {
        return super.request({
            url: '/lab-research/orders',
            use: getRequest,
            params: { page: page - 1, ...other }
        })
    }

    findById(orderId, params) {
        return super.request({
            url: `/lab-research/orders/${orderId}`,
            params
        })
    }

    findDefault(params) {
        return super.request({
            url: `/lab-research/orders/default`,
            params
        })
    }

    findIcdCodes(params) {
        return super.request({
            url: `/lab-research/orders/icd-codes`,
            params
        })
    }

    findSpecimensTypes(params) {
        return super.request({
            url: `/lab-research/orders/specimens-types`,
            params
        })
    }

    canAdd({ communityId, clientId }) {
        return super.request({
            url: getUrl({
                resources: [
                    { name: 'clients', id: clientId },
                    'lab-research',
                    'orders',
                    'can-add'
                ]
            }),
            params: { communityId }
        })
    }

    canView(params) {
        return super.request({
            url: `/lab-research/orders/can-view`,
            params
        })
    }

    canReview(params) {
        return super.request({
            url: `/lab-research/orders/can-review`,
            params
        })
    }

    save(data) {
        const isNew = isEmpty(data.id)

        return super.request({
            method: isNew ? 'POST' : 'PUT',
            url: '/lab-research/orders',
            body: data,
            type: 'json'
        })
    }

    count(params) {
        return super.request({
            url: '/lab-research/orders/count',
            params
        })
    }

    setReviewed(orderIds) {
        return super.request({
            method: 'PUT',
            url: `/lab-research/orders/review`,
            body: { orderIds }
        })
    }

    validateUniqInOrganization(params) {
        return super.request({
            url: '/lab-research/orders/validate-uniq-in-organization',
            params
        })
    }

    findTestResults({ orderId, page = FIRST_PAGE, ...other }) {
        return super.request({
            url: `/lab-research/orders/${orderId}/test-results`,
            params: { page: page - 1, ...other }
        })
    }

    findPendingReviewOrders(params) {
        return super.request({
            url: '/lab-research/orders/pending-review',
            params
        })
    }

    findCollectorSites(params) {
        return super.request({
            url: '/lab-research/orders/collector-sites',
            response: { extractDataOnly: true },
            params
        })
    }
}

export default new LabResearchOrderService()