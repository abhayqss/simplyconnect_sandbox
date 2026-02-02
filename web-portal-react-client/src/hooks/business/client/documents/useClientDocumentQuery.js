import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

function fetch({ documentId, ...params }) {
    return service.findById(documentId, params)
}

function useClientDocumentQuery(params, options) {
    return useQuery(['ClientDocument', params], () => fetch(params), options)
}

export default useClientDocumentQuery
