import BaseService from './BaseService'

import { PAGINATION } from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class ProspectMedicalContacts extends BaseService {
    find({ prospectId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/prospects/${prospectId}/medical-contacts`,
            params: { page: page - 1, size, ...other },
        })
    }

    findById(id, { prospectId }) {
        return super.request({
            url: `/prospects/${prospectId}/medical-contacts/${id}`
        })
    }

    count({ prospectId }) {
        return super.request({
            url: `/prospects/${prospectId}/medical-contacts/count`
        })
    }
}

export default new ProspectMedicalContacts()