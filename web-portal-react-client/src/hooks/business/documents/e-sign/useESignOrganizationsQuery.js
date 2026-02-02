import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findOrganizations(params)

export default function useDocumentESignOrganizationQuery(params, options) {
    return useQuery(['DocumentESign.Organizations', params], () => fetch(params), options)
}