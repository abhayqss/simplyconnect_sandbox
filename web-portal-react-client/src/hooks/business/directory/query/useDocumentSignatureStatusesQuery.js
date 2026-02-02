import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findDocumentSignatureStatuses(params)

export default function useDocumentSignatureStatusesQuery(params, options) {
    return useQuery(['DocumentSignatureStatuses', params], () => fetch(params), options)
}