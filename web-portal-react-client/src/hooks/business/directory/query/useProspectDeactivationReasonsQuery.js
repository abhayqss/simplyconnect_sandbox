import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = (params) => service.findProspectDeactivationReasons(
    params, { response: { extractDataOnly: true } }
)

export default function useProspectDeactivationReasonsQuery(params, options) {
    return useQuery(['Directory.ProspectDeactivationReasons', params], () => fetch(params), options)
}