import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

const fetch = ({ communityId, parentFolderId }) => service.canAdd({ communityId, parentFolderId })

export default function useCanAddDocumentFoldersQuery(params, options) {
    return useQuery(['CanAddDocumentFolders', params], () => fetch(params), options)
}