import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = () => service.findClientStatuses(
    { response: { extractDataOnly: true } }
)

export default function useClientStatusesQuery(params, options) {
    return useQuery(['Directory.ClientStatuses', params], () => fetch(params), options)
}
