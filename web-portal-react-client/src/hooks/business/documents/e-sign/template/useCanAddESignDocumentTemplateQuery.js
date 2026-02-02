import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.canAddTemplate(params)

export default function useCanAddESignDocumentTemplateQuery(params, options) {
    return useQuery(['ESignDocumentTemplateFile.CanAdd', params], () => fetch(params), options)
}