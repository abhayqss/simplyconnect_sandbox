import BaseService from './BaseService'

import { PAGINATION } from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class ClientMedicalContacts extends BaseService {
    find({ clientId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/clients/${clientId}/medical-contacts`,
            params: { page: page - 1, size, ...other },
        })
    }

    findById(id, { clientId }) {
        return super.request({
            url: `/clients/${clientId}/medical-contacts/${id}`
        })
    }

    count({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/medical-contacts/count`
        })
    }
}

export default new ClientMedicalContacts()