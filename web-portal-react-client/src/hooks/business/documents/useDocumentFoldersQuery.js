import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

const fetch = params => {
    return service.find(params)
}

function useDocumentFoldersQuery(params, options) {
    return useQuery(['DocumentFolders', params], () => fetch(params), options)
}

export default useDocumentFoldersQuery
