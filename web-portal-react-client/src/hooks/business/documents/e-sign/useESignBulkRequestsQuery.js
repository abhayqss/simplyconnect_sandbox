import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findBulkRequests(params)

export default function useESignBulkRequestsQuery(params, options) {
    return useQuery(['DocumentSignature.BulkRequests', params], () => fetch(params), options)
}