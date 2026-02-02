import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findESignDocumentTemplateToolboxSignerFieldTypes(params)

export default function useESignDocumentTemplateSignerFieldTypesQuery(params, options) {
    return useQuery(['ESignDocumentTemplate.SignerFieldTypesQuery', params], () => fetch(params), options)
}