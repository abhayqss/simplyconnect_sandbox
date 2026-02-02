import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findClients(
    params, { response: { extractDataOnly: true } }
)

function useClientsQuery(params, options) {
    return useQuery(['Directory.Clients', params], () => fetch(params), options)
}

export default useClientsQuery
