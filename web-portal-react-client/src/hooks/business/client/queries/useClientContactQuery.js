import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientService'

const fetch = params => service.findContact(params)

function useClientContactDetailsQuery(params, options) {
    return useQuery(['Client.Contact', params], () => fetch(params), options)
}

export default useClientContactDetailsQuery
