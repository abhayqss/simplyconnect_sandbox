import { Shape } from './types'

import service from 'services/ClientService'

export default Shape().test({
    name: 'uniqSSNInCommunity',
    test: async ({ ssn, clientId, communityId }) => {
        try {
            let response = await service.validateUniqInCommunity({ ssn, clientId, communityId })

            return response.data.ssn
        } catch (error) {
            return false
        }
    },
    message: 'The client with the SSN entered already exists in the community.',
    exclusive: true,
})
