import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

const fetch = ({ folderId }) => {
    return service.findById(folderId)
}

function useDocumentFolderQuery(params, options) {
    return useQuery(['DocumentFolder', params], () => fetch(params), options)
}

export default useDocumentFolderQuery
