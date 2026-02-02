import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findTemplates(params)

export default function useDocumentTemplatesQuery(params, options) {
    return useQuery(['DocumentTemplates', params], () => fetch(params), options)
}