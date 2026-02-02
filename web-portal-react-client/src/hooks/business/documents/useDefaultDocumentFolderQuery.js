import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

const fetch = ({ communityId, parentFolderId, isSecurityEnabled }) => {
    return service.findDefault({ communityId, parentFolderId, isSecurityEnabled })
}

function useDefaultDocumentFolderQuery(params, options) {
    return useQuery(['DefaultDocumentFolder', params], () => fetch(params), options)
}

export default useDefaultDocumentFolderQuery
