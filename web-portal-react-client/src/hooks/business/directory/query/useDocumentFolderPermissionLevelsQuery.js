import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = () => {
    return service.findFolderPermissionLevels()
}

function useDocumentFolderPermissionLevelsQuery(params, options) {
    return useQuery(['Document.Folder.Permission.Levels', params], () => fetch(params), options)
}

export default useDocumentFolderPermissionLevelsQuery 
