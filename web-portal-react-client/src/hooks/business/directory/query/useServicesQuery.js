import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findServices(params)

function useServicesQuery(params, options) {
	return useQuery(['Directory.Services', params], () => fetch(params), options)
}

export default useServicesQuery
