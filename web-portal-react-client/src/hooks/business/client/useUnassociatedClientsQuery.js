import { useQuery } from 'hooks/common'

import service from 'services/ClientService'

const fetch = params => service.findUnassociated(
    { ...params, response: { extractDataOnly: true } }
)

function useUnassociatedClientsQuery(params, options) {
    return useQuery('UnassociatedClients', params, {
        fetch, ...options,
    })
}

export default useUnassociatedClientsQuery
