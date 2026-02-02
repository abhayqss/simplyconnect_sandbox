import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = ({ requestId }) => service.findSignatureRequestById(requestId)

export default function useESignRequestQuery(params, options) {
    return useQuery(['DocumentSignature.Request', params], () => fetch(params), options)
}