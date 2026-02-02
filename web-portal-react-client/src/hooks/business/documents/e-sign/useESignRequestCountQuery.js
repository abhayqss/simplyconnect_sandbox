import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findSignatureRequestCount(params)

export default function useDocumentTemplateCountQuery(params, options) {
    return useQuery(['DocumentSignature.RequestCount', params], () => fetch(params), options)
}