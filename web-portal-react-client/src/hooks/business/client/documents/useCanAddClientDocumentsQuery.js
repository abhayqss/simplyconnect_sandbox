import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

const fetch = params => service.canAdd(
    params, { response: { extractDataOnly: true } }
)

export default function useCanAddClientDocumentsQuery(params, options) {
    return useQuery(['Client.CanAddDocuments', params], () => fetch(params), options)
}