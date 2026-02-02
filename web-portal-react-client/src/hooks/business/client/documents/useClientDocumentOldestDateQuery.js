import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

function fetch(params) {
    return service.findOldestDate(params)
}

function useClientDocumentOldestDateQuery(params, options) {
    return useQuery(['Client.Document.OldestDate', params], () => fetch(params), options)
}

export default useClientDocumentOldestDateQuery
