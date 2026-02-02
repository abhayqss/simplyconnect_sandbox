import BaseService from './BaseService'

import {
    PAGINATION,
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION
const { ZIP } = ALLOWED_FILE_FORMATS

export class ClientDocumentService extends BaseService {
    find({ clientId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/clients/${clientId}/documents`,
            params: { page: page - 1, size, ...other }
        })
    }

    findById(documentId, { clientId }) {
        return super.request({
            url: `/clients/${clientId}/documents/${documentId}`,
            response: { extractDataOnly: true }
        })
    }

    downloadById(documentId, { mimeType, clientId }) {
        return super.request({
            url: `/clients/${clientId}/documents/${documentId}/download`,
            type: mimeType,
            params: { aggregated: true }
        })
    }

    downloadMultiple({ clientId, ...params }) {
        return super.request({
            url: `/clients/${clientId}/documents/download`,
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[ZIP],
            params
        })
    }

    downloadHtmlById(documentId, { clientId }) {
        return super.request({
            url: `/clients/${clientId}/documents/${documentId}/cda-view`,
            params: { aggregated: true }
        })
    }

    findHistory({ clientId, documentId, page = FIRST_PAGE, size = 10 }) {
        return super.request({
            url: `/clients/${clientId}/documents/${documentId}/history`,
            params: { page: page - 1, size }
        })
    }

    count({ clientId, ...params }, options) {
        return super.request({
            url: `/clients/${clientId}/documents/count`,
            params,
            ...options
        })
    }

    save(data, { clientId }) {
        return super.request({
            method: data.id ? 'PUT' : 'POST',
            url: `/clients/${clientId}/documents`,
            body: data,
            type: 'multipart/form-data',
            responseTimeout: 1200000
        })
    }

    deleteById(documentId, { clientId, ...params }) {
        return super.request({
            method: 'DELETE',
            url: `/clients/${clientId}/documents/${documentId}`,
            params
        })
    }

    restoreById(documentId, { clientId }) {
        return super.request({
            method: 'POST',
            url: `/clients/${clientId}/documents/${documentId}/restore`
        })
    }

    canAdd({ clientId }, options) {
        return super.request({
            url: `/clients/${clientId}/documents/can-add`,
            ...options
        })
    }

    findOldestDate({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/documents/oldest/date`,
            response: { extractDataOnly: true }
        })
    }

    canView({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/documents/can-view`,
            response: { extractDataOnly: true }
        })
    }

    canDownloadCCD({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/documents/ccd/can-download`,
            response: { extractDataOnly: true }
        })
    }

    canDownloadFacesheet({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/documents/facesheet/can-download`,
            response: { extractDataOnly: true }
        })
    }
}

const service = new ClientDocumentService()
export default service