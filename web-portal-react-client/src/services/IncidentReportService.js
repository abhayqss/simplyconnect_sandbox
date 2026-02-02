import { noop } from 'underscore'

import { isEmpty } from 'lib/utils/Utils'

import BaseService from './BaseService'

import {
    PAGINATION,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class IncidentReportService extends BaseService {
    find(
        { page = FIRST_PAGE, ...other },
        { getRequest = noop } = {}
    ) {
        return super.request({
            use: getRequest,
            url: '/incident-reports',
            params: { page: page - 1, ...other }
        })
    }

    findById(reportId, params) {
        return super.request({
            url: `/incident-reports/${reportId}`,
            params
        })
    }

    findDefault(params) {
        return super.request({
            url: `/incident-reports/default`,
            params
        })
    }

    findHistory ({ reportId, page = FIRST_PAGE, size = 10 }) {
        return super.request({
            url:`/incident-reports/${reportId}/history`,
            params: { page: page - 1, size },
        })
    }

    downloadById(reportId, { format, ...other }) {
        return super.request({
            url: `/incident-reports/${reportId}/download`,
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[format],
            other
        })
    }

    save(data, isDraft) {
        const isNew = isEmpty(data.id)

        const url = `/incident-reports${isDraft ? '' : '/submit'}`

        return super.request({
            url,
            method: isNew ? 'POST' : 'PUT',
            body: data,
            type: 'multipart/form-data'
        })
    }

    joinToConversationById(reportId) {
        return super.request({
            url: `/incident-reports/${reportId}/conversation/join`,
            type: 'multipart/form-data',
            method: 'POST'
        })
    }

    deleteById(reportId) {
        return super.request({
            method: 'DELETE',
            url: `/incident-reports/${reportId}`
        })
    }

    count(params) {
        return super.request({
            url: '/incident-reports/count',
            params
        })
    }

    canView(params) {
        return super.request({
            url: `/incident-reports/can-view`,
            params
        })
    }

    findOldestDate(params) {
        return super.request({
            url: '/incident-reports/oldest/date',
            params
        })
    }

    findLatestDate(params) {
        return super.request({
            url: '/incident-reports/newest/date',
            params
        })
    }

    findIncidentPictureById(pictureId) {
        return super.request({
            url: `/incident-reports/incident-pictures/${pictureId}`
        })
    }
}

export default new IncidentReportService()