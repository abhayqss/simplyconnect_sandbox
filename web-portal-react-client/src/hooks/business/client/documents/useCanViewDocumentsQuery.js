import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

const fetch = params => service.canView(params)

function useCanViewDocumentsQuery(params, options) {
    return useQuery(['Client.CanViewDocuments', params], () => fetch(params), options)
}

export default useCanViewDocumentsQuery