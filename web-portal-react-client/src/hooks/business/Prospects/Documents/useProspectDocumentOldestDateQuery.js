import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectDocumentService'

function fetch(params) {
    return service.findOldestDate(params)
}

function useProspectDocumentOldestDateQuery(params, options) {
    return useQuery(['Prospect.Document.OldestDate', params], () => fetch(params), options)
}

export default useProspectDocumentOldestDateQuery
