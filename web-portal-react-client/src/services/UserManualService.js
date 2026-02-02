import { isEmpty } from 'lib/utils/Utils'
import { ALLOWED_FILE_FORMAT_MIME_TYPES } from 'lib/Constants'

import BaseService from './BaseService'

export class UserManualService extends BaseService {
    find(params) {
        return super.request({
            url: '/help/user-manuals', params
        })
    }

    downloadById(manualId, { mimeType }) {
        return super.request({
            type: mimeType,
            url: `/help/user-manuals/${manualId}`
        })
    }

    save(data) {
        return super.request({
            method: isEmpty(data.id) ? 'POST' : 'PUT',
            url: '/help/user-manuals',
            body: data,
            type: 'multipart/form-data'
        })
    }

    deleteById(manualId) {
        return super.request({
            method: 'DELETE',
            url: `/help/user-manuals/${manualId}`
        })
    }

    canUpload() {
        return super.request({
            url: '/help/user-manuals/can-upload'
        })
    }

    canDelete() {
        return super.request({
            url: '/help/user-manuals/can-delete'
        })
    }
}

export default new UserManualService()

