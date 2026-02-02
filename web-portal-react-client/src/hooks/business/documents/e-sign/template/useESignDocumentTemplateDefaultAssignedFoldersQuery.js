import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

function fetch(params) {
    return service.findDefaultAssignedTemplateFolders(params)
}

function useESignDocumentTemplateDefaultAssignedFoldersQuery(params, options) {
    return useQuery(['ESignDocumentTemplateDefaultAssignedFolders', params], () => fetch(params), options)
}

export default useESignDocumentTemplateDefaultAssignedFoldersQuery
