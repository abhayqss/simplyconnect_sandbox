import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientService'

const fetch = ({ clientId }) => service.findById(clientId, {
    response: { extractDataOnly: true }
})

function useClientQuery(params, options) {
    return useQuery(['Client', params], () => fetch(params), options)
}

export default useClientQuery
