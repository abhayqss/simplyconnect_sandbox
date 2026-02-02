import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderAggregatedService'

function fetch(params) {
    return service.count(params, {
        response: { extractDataOnly: true }
    })
}

function useDocumentCountQuery(params, options) {
    return useQuery(['Document.Count', params], () => fetch(params), options)
}

export default useDocumentCountQuery
