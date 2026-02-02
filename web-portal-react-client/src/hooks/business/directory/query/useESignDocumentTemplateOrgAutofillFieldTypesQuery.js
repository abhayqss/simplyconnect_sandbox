import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findESignDocumentTemplateOrganizationAutofillFieldTypes(params)

export default function useESignDocumentTemplateOrgAutofillFieldTypesQuery(params, options) {
    return useQuery(['ESignDocumentTemplate.OrganizationAutofillFieldsQuery', params], () => fetch(params), options)
}