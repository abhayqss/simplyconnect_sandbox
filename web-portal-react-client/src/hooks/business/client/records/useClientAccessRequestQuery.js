import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientAccessRequestService'

const fetch = ({ requestId, ...params } = {}) => service.findById(requestId, params)

function useClientAccessRequestQuery(params, options) {
	return useQuery(['Client.Access.Request', params], () => fetch(params), options)
}

export default useClientAccessRequestQuery