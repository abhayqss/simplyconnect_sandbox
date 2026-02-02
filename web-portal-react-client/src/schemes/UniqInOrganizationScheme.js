import { Shape } from './types'

import service from 'services/OrganizationService'

export default function UniqInOrganizationScheme(field, errorMessage) {
    return Shape().test({
        name: 'UniqInOrganizationScheme',
        test: async (data) => {
            try {
                let response = await service.validateUniq(data)
    
                return response.data[field]
            } catch (error) {
                return false
            }
        },
        message: errorMessage,
        exclusive: true,
    })
}
