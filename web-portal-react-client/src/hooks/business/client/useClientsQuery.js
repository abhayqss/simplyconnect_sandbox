import { useQuery } from 'hooks/common'

import service from 'services/ClientService'

const fetch = params => service.find(
    { ...params, response: { extractDataOnly: true } }
)

function useClientsQuery(params, options) {
    return useQuery('Clients', params, {
        fetch,
        ...options,
    })
}

export default useClientsQuery
