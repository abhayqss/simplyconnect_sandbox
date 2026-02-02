import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectDocumentService'

function fetch({ documentId, ...params }) {
    return service.findById(documentId, params)
}

function useProspectDocumentQuery(params, options) {
    return useQuery(['ProspectDocument', params], () => fetch(params), options)
}

export default useProspectDocumentQuery
