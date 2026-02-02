import BaseService from './BaseService'

import {
    PAGINATION,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { lc } from 'lib/utils/Utils'

const { FIRST_PAGE } = PAGINATION

export class SDoHReportService extends BaseService {
    find({ page = FIRST_PAGE, ...other }) {
        return super.request({
            url: '/sdoh/reports',
            params: { page: page - 1, ...other }
        })
    }

    downloadById(reportId, { format, ...other }) {
        return super.request({
            url: `/sdoh/reports/${reportId}/download-${lc(format)}`,
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[format],
            other
        })
    }

    markAsSent(reportId, params) {
        return super.request({
            method: 'PUT',
            url: `/sdoh/reports/${reportId}/mark-as-sent`,
            body: params
        })
    }

    canMarkAsSent(reportId, params) {
        return super.request({
            url: `/sdoh/reports/${reportId}/can-mark-as-sent`,
            params
        })
    }

    canView() {
        return super.request({
            url: '/sdoh/reports/can-view'
        })
    }
}

export default new SDoHReportService()

