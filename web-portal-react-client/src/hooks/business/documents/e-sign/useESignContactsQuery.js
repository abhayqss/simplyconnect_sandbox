import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findContacts(params)

export default function useESignContactsQuery(params, options) {
    return useQuery(['DocumentESign.Contacts', params], () => fetch(params), options)
}