import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = (params) => service.findProspectStatuses(
    params, { response: { extractDataOnly: true } }
)

export default function useProspectStatusesQuery(params, options) {
    return useQuery(['Directory.ProspectStatuses', params], () => fetch(params), options)
}
