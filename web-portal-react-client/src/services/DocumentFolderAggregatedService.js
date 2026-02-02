import { BaseService } from 'services'

import {
    PAGINATION,
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION
const { ZIP } = ALLOWED_FILE_FORMATS

class DocumentFolderAggregatedService extends BaseService {
    find({ page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/documents-&-folders`,
            params: { page: page - 1, size, ...other }
        })
    }

    downloadMultiple(params) {
        return super.request({
            url: `/documents-&-folders/download`,
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[ZIP],
            params
        })
    }

    findOldestDate(params) {
        return super.request({
            params,
            url: `/documents-&-folders/oldest/date`,
            response: { extractDataOnly: true }
        })
    }

    count(params) {
        return super.request({
            url: `/documents-&-folders/count`,
            params,
            response: { extractDataOnly: true }
        })
    }

    canView() {
        return super.request({
            url: `/documents-&-folders/can-view`,
            response: { extractDataOnly: true }
        })
    }
}

const service = new DocumentFolderAggregatedService()
export default service