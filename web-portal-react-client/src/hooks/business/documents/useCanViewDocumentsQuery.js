import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderAggregatedService'

const fetch = () => service.canView()

export default function useCanViewDocumentsQuery(params, options) {
    return useQuery(['CanViewDocuments', params], () => fetch(params), options)
}