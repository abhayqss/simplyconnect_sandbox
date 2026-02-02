import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findDocumentSignatureRequestNotificationMethods(params)

export default function useDocumentSignatureRequestNotificationMethodsQuery(params, options) {
    return useQuery(['DocumentSignatureRequestNotificationMethods', params], () => fetch(params), options)
}