import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

const fetch = params => service.canView(params)

export default function useCanAddDocumentFoldersQuery(params, options) {
    return useQuery(['CanViewDocumentFolder', params], () => fetch(params), options)
}