import { Shape } from './types'

import service from 'services/ClientService'

export default Shape().test({
    name: 'uniqEmailWithinOrganization',
    test: async (data) => {
        try {
            let response = await service.validateUniqInOrganization(data)

            return response.data.email
        } catch (error) {
            return false
        }
    },
    message: 'The email must be unique within the organization.',
    exclusive: true,
})
