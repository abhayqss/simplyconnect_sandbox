import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findESignDocumentTemplateToolboxRequesterFieldTypes(params)

export default function useESignDocumentTemplateRequesterFieldTypesQuery(params, options) {
    return useQuery(['ESignDocumentTemplate.RequesterFieldTypesQuery', params], () => fetch(params), options)
}