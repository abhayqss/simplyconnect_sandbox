import { noop } from 'underscore'

import { PAGINATION } from 'lib/Constants'
import { isEmpty } from 'lib/utils/Utils'

import BaseService from './BaseService'

const { FIRST_PAGE } = PAGINATION

export class ContactService extends BaseService {
    find(
        { name, page = FIRST_PAGE, sort, size = 10, ...other },
        { getRequest = noop } = {}
    ) {
        return super.request({
            url: `/contacts`,
            use: getRequest,
            params: { name, page: page - 1, size, sort, ...other }
        })
    }

    findById (contactId, options) {
        return super.request({
            url: `/contacts/${contactId}`,
            mockParams: { id: contactId },
            ...options
        })
    }

    count () {
        return super.request({
            url: `/contacts/count`
        })
    }

    save (data) {
        return super.request({
            method: isEmpty(data.id) ? 'POST' : 'PUT',
            url: `/contacts`,
            body: data,
            type: 'multipart/form-data'
        })
    }

    canAdd (params, options) {
        return super.request({
            url: '/contacts/can-add',
            ...options,
            params
        })
    }

    invite (contactId) {
        return super.request({
            method: 'POST',
            url: `/contacts/${contactId}/invite`,
        })
    }

    validateUniq (data) {
        return super.request({
            url: '/contacts/validate-uniq',
            params: data
        })
    }

    findLocationById(contactId, params) {
        return super.request({
            url: `/contacts/${contactId}/location`,
            response: { extractDataOnly: true },
            params
        })
    }

    findQAUnavailableRoles(params, options) {
        return super.request({
            url: `/contacts/qa-unavailable-roles`,
            ...options,
            params
        })
    }

    modifyPassword(id, password) {
        return super.request({
            method: 'POST',
            url: `/auth/password/modify`,
            body:{
                employeeId:id,
                password,
            }
        })
    }
    manualCreatePassword(id, password) {
        return super.request({
            method: 'POST',
            url: `/auth/password/manualCreate`,
            body:{
                contactId:id,
                password,
            }
        })
    }
}

const service = new ContactService()
export default service
