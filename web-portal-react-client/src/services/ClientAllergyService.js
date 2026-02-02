import BaseService from './BaseService'

import { PAGINATION } from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class ClientAllergyService extends BaseService {
    find({ clientId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/clients/${clientId}/allergies`,
            params: { page: page - 1, size, ...other },
        })
    }

    findById(allergyId, { clientId }) {
        return super.request({
            url: `/clients/${clientId}/allergies/${allergyId}`
        })
    }

    count({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/allergies/count`
        })
    }
}

const service = new ClientAllergyService()
export default service