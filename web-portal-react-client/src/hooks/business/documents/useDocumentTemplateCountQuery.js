import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findTemplateCount(params)

export default function useDocumentTemplateCountQuery(params, options) {
    return useQuery(['DocumentTemplateCount', params], () => fetch(params), options)
}