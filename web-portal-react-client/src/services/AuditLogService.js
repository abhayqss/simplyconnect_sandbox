import {
    PAGINATION,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import BaseService from './BaseService'

const { FIRST_PAGE } = PAGINATION

export class AuditLogService extends BaseService {
    find({ page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: '/audit-logs',
            method: 'POST',
            body: { page: page - 1, size, ...other }
        })
    }

    count() {
        return super.request({
            url: '/audit-logs/count',
            response: { extractDataOnly: true }
        })
    }

    findDateOfOldest(params) {
        return super.request({
            url: '/audit-logs/oldest-date',
            response: { extractDataOnly: true },
            params
        })
    }

    canView(params) {
        return super.request({
            url: '/audit-logs/can-view',
            params
        })
    }

    download({ page = FIRST_PAGE, size = 10, format, ...other }) {
        return super.request({
            url: '/audit-logs/export',
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[format],
            params: { page: page - 1, size, ...other }
        })
    }
}

export default new AuditLogService()