import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import BaseService from './BaseService'

const { ZIP } = ALLOWED_FILE_FORMATS

export class DocumentTemplateService extends BaseService {
    findById(templateId, params) {
        return super.request({
            url: `/document-templates/${templateId}`,
            response: { extractDataOnly: true },
            params
        })
    }

    downloadMultiple(params) {
        return super.request({
            url: '/document-templates/download',
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[ZIP],
            params
        })
    }

    downloadById(templateId, params) {
        return super.request({
            url: `/document-templates/${templateId}/download`,
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[ZIP],
            params
        })
    }

    assignToFolder({ templateId, folderId }) {
        return super.request({
            method: 'POST',
            body: { folderId },
            response: { extractDataOnly: true },
            url: `/document-templates/${templateId}/assign`
        })
    }
}

const service = new DocumentTemplateService()
export default service