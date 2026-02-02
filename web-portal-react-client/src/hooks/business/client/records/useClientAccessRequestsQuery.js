import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientAccessRequestService'

const fetch = params => service.find(params)

function useClientAccessRequestsQuery(params, options) {
	return useQuery(['Client.Access.Requests', params], () => fetch(params), options)
}

export default useClientAccessRequestsQuery