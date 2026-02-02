import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findOrganizations(
    params, { response: { extractDataOnly: true } }
)

export default function useOrganizationsQuery(params, options) {
    return useQuery(['Directory.Organizations', params], () => fetch(params), options)
}