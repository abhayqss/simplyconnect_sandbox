import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findESignDocumentTemplateAutofillFieldTypes(params)

export default function useESignDocumentTemplateAutofillFieldTypesQuery(params, options) {
    return useQuery(['ESignDocumentTemplate.AutofillFieldTypes', params], () => fetch(params), options)
}