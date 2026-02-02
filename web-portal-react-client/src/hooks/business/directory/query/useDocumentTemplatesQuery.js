import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findDocumentTemplates(params)

export default function useDocumentTemplatesQuery(params, options) {
    return useQuery(['DocumentTemplates', params], () => fetch(params), options)
}