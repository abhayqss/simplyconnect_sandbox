import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findClientDeactivationReasons(
    params, { response: { extractDataOnly: true } }
)

export default function useClientDeactivationReasonsQuery(params, options) {
    return useQuery(['Directory.ClientDeactivationReasons', params], () => fetch(params), options)
}