import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentService'

function fetch({ documentId }) {
    return service.findById(documentId)
}

function useDocumentQuery(params, options) {
    return useQuery(['Document', params], () => fetch(params), options)
}

export default useDocumentQuery
