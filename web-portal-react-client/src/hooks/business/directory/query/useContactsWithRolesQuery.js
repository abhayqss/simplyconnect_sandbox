import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findContactsWithRoles(params)

function useContactsWithRolesQuery(params, options) {
    return useQuery(['Directory.ContactsWithRoles', params], () => fetch(params), options)
}

export default useContactsWithRolesQuery
