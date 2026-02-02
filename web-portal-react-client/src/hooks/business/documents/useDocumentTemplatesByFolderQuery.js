import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findTemplatesTree(params)

export default function useDocumentTemplatesByFolderQuery(params, options) {
    return useQuery(['DocumentTemplates', params], () => fetch(params), options)
}
