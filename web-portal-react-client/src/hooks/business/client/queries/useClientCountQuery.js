import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientService'

const fetch = () => service.count()

function useClientCountQuery(params, options) {
    return useQuery(['Clients.Count', params], () => fetch(params), options)
}

export default useClientCountQuery
