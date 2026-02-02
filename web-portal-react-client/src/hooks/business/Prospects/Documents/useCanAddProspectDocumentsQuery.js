import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectDocumentService'

const fetch = (params) => service.canAdd(
    params, { response: { extractDataOnly: true } }
)

export default function useCanAddProspectDocumentsQuery(params, options) {
    return useQuery(['Prospect.CanAddDocuments', params], () => fetch(params), options)
}