import BaseService from './BaseService'
import { PAGINATION } from 'lib/Constants'
import { isEmpty } from 'lib/utils/Utils'

const { FIRST_PAGE } = PAGINATION

export class CategoryService extends BaseService {
    find({ page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/categories`,
            params: { page: page - 1, size, ...other }
        })
    }

    findById(id) {
        return super.request({
            url: `/categories/${id}`,
            response: { extractDataOnly: true }
        })
    }

    save(data) {
        const isNew = isEmpty(data.id)

        return super.request({
            method: isNew ? 'POST' : 'PUT' ,
            url: `/categories`,
            body: data,
            type: 'multipart/form-data',
            response: { extractDataOnly: true }
        })
    }

    remove(id) {
        return super.request({
            url: `/categories/${id}`,
            method: 'DELETE',
            response: { extractDataOnly: true }
        })
    }

    canAdd(params) {
        return super.request({
            url: `/categories/can-add`,
            response: { extractDataOnly: true },
            params
        })
    }

    canEdit(params) {
        return super.request({
            url: `/categories/can-add`,
            response: { extractDataOnly: true },
            params
        })
    }

    canView(params) {
        return super.request({
            url: `/categories/can-view`,
            response: { extractDataOnly: true },
            params
        })
    }

    validateUniq ({ name, categoryId, organizationId }) {
        return super.request({
            url: '/categories/validate-uniq',
            params: { name, categoryId, organizationId }
        })
    }
}

const service = new CategoryService()
export default service