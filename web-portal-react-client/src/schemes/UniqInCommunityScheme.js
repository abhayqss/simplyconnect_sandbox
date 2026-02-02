import { all } from 'underscore'

import { Shape } from './types'

import service from 'services/ClientService'

import { interpolate, omitEmptyProps } from 'lib/utils/Utils'

const NON_UNIQ = '$0 must be unique within the community.'

const replacer = (message, value) => interpolate(message, value)

export default field => Shape().test({
    name: 'uniqInCommunity',
    test: async ({ memberNumber, medicareNumber, medicaidNumber, clientId, communityId }) => {
        let params = omitEmptyProps({ memberNumber, medicareNumber, medicaidNumber })

        try {
            let response = await service.validateUniqInCommunity({
                clientId,
                communityId,
                ...params,
            })
            
            return all(response.data, o => o === true)
        } catch (error) {
            return false
        }
    },
    message: replacer(NON_UNIQ, field),
    exclusive: true,
})
