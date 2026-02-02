import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.canAddSignature(params)

export default function useCanAddESignRequestQuery(params, options) {
    return useQuery(['DocumentESign.CanAddRequest', params], () => fetch(params), options)
}