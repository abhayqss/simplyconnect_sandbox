import { Shape } from './types'

import service from 'services/CategoryService'

export default function UniqOrganizationCategoryScheme(field) {
    return Shape().test({
        name: 'UniqOrganizationCategoryScheme',
        test: async (data) => {
            try {
                let response = await service.validateUniq(data)
    
                return response.data
            } catch (error) {
                return false
            }
        },
        message: 'The category already exists. Please enter a unique name.',
        exclusive: true,
    })
}
