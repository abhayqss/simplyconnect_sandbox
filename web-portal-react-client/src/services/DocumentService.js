import { PAGINATION } from 'lib/Constants'

import BaseService from './BaseService'

const { FIRST_PAGE } = PAGINATION

export class DocumentService extends BaseService {
    find({ clientId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/documents`,
            params: { page: page - 1, size, ...other }
        })
    }

    findById(documentId) {
        return super.request({
            url: `/documents/${documentId}`,
            response: { extractDataOnly: true }
        })
    }

    downloadById(documentId, { mimeType }) {
        return super.request({
            url: `/documents/${documentId}/download`,
            type: mimeType,
            params: { aggregated: true }
        })
    }

    downloadHtmlById(documentId) {
        return super.request({
            url: `/documents/${documentId}/cda-view`,
            params: { aggregated: true }
        })
    }

    findHistory({ documentId, page = FIRST_PAGE, size = 10 }) {
        return super.request({
            url: `/documents/${documentId}/history`,
            params: { page: page - 1, size }
        })
    }

    count(params) {
        return super.request({
            params,
            url: `/documents/count`,
            response: { extractDataOnly: true }
        })
    }

    save(data) {
        return super.request({
            method: data.id ? 'PUT' : 'POST',
            url: `/documents`,
            body: data,
            type: 'multipart/form-data',
            responseTimeout: 1200000
        })
    }

    deleteById(documentId, params) {
        return super.request({
            method: 'DELETE',
            url: `/documents/${documentId}`,
            params
        })
    }

    restoreById(documentId) {
        return super.request({
            method: 'POST',
            url: `/documents/${documentId}/restore`
        })
    }

    canAdd(params) {
        return super.request({
            url: `/documents/can-add`,
            response: { extractDataOnly: true },
            params
        })
    }
}

const service = new DocumentService()
export default service