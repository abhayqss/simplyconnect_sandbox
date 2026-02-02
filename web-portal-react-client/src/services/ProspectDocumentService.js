import BaseService from './BaseService'

import {
    PAGINATION,
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION
const { ZIP } = ALLOWED_FILE_FORMATS

export class ProspectDocumentService extends BaseService {
    find({ prospectId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/prospects/${prospectId}/documents`,
            params: { page: page - 1, size, ...other }
        })
    }

    findById(documentId, { prospectId }) {
        return super.request({
            url: `/prospects/${prospectId}/documents/${documentId}`,
            response: { extractDataOnly: true }
        })
    }

    downloadById(documentId, { mimeType, prospectId }) {
        return super.request({
            url: `/prospects/${prospectId}/documents/${documentId}/download`,
            type: mimeType,
            params: { aggregated: true }
        })
    }

    downloadMultiple({ prospectId, ...params }) {
        return super.request({
            url: `/prospects/${prospectId}/documents/download`,
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[ZIP],
            params
        })
    }

    downloadHtmlById(documentId, { prospectId }) {
        return super.request({
            url: `/prospects/${prospectId}/documents/${documentId}/cda-view`,
            params: { aggregated: true }
        })
    }

    findHistory({ prospectId, documentId, page = FIRST_PAGE, size = 10 }) {
        return super.request({
            url: `/prospects/${prospectId}/documents/${documentId}/history`,
            params: { page: page - 1, size }
        })
    }

    count({ prospectId, ...params }, options) {
        return super.request({
            url: `/prospects/${prospectId}/documents/count`,
            response: { extractDataOnly: true },
            params,
            ...options
        })
    }

    save(data, { prospectId }) {
        return super.request({
            method: data.id ? 'PUT' : 'POST',
            url: `/prospects/${prospectId}/documents`,
            body: data,
            type: 'multipart/form-data',
            responseTimeout: 1200000
        })
    }

    deleteById(documentId, { prospectId, ...params }) {
        return super.request({
            method: 'DELETE',
            url: `/prospects/${prospectId}/documents/${documentId}`,
            params
        })
    }

    restoreById(documentId, { prospectId }) {
        return super.request({
            method: 'POST',
            url: `/prospects/${prospectId}/documents/${documentId}/restore`
        })
    }

    canAdd({ prospectId }, options) {
        return super.request({
            url: `/prospects/${prospectId}/documents/can-add`,
            response: { extractDataOnly: true },
            ...options
        })
    }

    findOldestDate({ prospectId }) {
        return super.request({
            url: `/prospects/${prospectId}/documents/oldest/date`,
            response: { extractDataOnly: true }
        })
    }
}

const service = new ProspectDocumentService()
export default service