import BaseService from './BaseService'

import {
    PAGINATION,
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION
const { ZIP } = ALLOWED_FILE_FORMATS

export class ClientExpenseService extends BaseService {
    find({ clientId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/clients/${clientId}/expenses`,
            params: { page: page - 1, size, ...other }
        })
    }

    findById(expenseId, { clientId }) {
        return super.request({
            url: `/clients/${clientId}/expenses/${expenseId}`,
            response: { extractDataOnly: true }
        })
    }

    count({ clientId, ...params }) {
        return super.request({
            url: `/clients/${clientId}/expenses/count`,
            response: { extractDataOnly: true },
            params
        })
    }

    total({ clientId, ...params }) {
        return super.request({
            url: `/clients/${clientId}/expenses/total`,
            response: { extractDataOnly: true },
            params
        })
    }

    canView({ clientId, ...params }) {
        return super.request({
            url: `/clients/${clientId}/expenses/can-view`,
            response: { extractDataOnly: true },
            params
        })
    }

    canAdd({ clientId, ...params }) {
        return super.request({
            url: `/clients/${clientId}/expenses/can-add`,
            response: { extractDataOnly: true },
            params
        })
    }

    save(data, { clientId }) {
        return super.request({
            method: data.id ? 'PUT' : 'POST',
            url: `/clients/${clientId}/expenses`,
            body: data,
            type: 'multipart/form-data',
            responseTimeout: 1200000,
            response: { extractDataOnly: true }
        })
    }
}

const service = new ClientExpenseService()
export default service