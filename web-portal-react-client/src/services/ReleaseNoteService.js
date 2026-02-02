import BaseService from './BaseService'
import { isEmpty } from "../lib/utils/Utils";

export class ReleaseNoteService extends BaseService {
    find(params) {
        return super.request({
            url: '/help/release-notes', params
        })
    }

    findById(noteId) {
        return super.request({
            url: `/help/release-notes/${noteId}`
        })
    }

    downloadById(noteId, { mimeType }) {
        return super.request({
            type: mimeType,
            url: `/help/release-notes/${noteId}/download`
        })
    }

    save(data) {
        return super.request({
            method: isEmpty(data.id) ? 'POST' : 'PUT',
            url: '/help/release-notes',
            body: data,
            type: 'multipart/form-data'
        })
    }

    deleteById(noteId) {
        return super.request({
            method: 'DELETE',
            url: `/help/release-notes/${noteId}`
        })
    }

    canUpload() {
        return super.request({
            url: '/help/release-notes/can-upload'
        })
    }

    canDelete() {
        return super.request({
            url: '/help/release-notes/can-delete'
        })
    }
}

export default new ReleaseNoteService()

