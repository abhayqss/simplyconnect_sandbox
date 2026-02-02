import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findSystemRoles(
    params, { response: { extractDataOnly: true } }
)

export default function useSystemRolesQuery(params, options) {
    return useQuery(['Directory.SystemRoles', params], () => fetch(params), options)
}
