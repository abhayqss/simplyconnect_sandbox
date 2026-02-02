import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderAggregatedService'

function fetch(params) {
    return service.findOldestDate(params)
}

function useDocumentOldestDateQuery(params, options) {
    return useQuery(['Document.OldestDate', params], () => fetch(params), options)
}

export default useDocumentOldestDateQuery
