import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findContacts(
    params, { response: { extractDataOnly: true } }
)

function useContactsQuery(params, options) {
    return useQuery(['Directory.Contacts', params], () => fetch(params), options)
}

export default useContactsQuery
