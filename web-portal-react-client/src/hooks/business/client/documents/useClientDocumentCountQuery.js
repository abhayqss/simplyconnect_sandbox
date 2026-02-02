import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

function fetch(params) {
    return service.count(params, {
        response: { extractDataOnly: true }
    })
}

function useClientDocumentCountQuery(params, options) {
    return useQuery(['Client.Document.Count', params], () => fetch(params), options)
}

export default useClientDocumentCountQuery
