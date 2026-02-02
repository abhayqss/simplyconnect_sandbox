import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findInsuranceNetworks(params)

function useInsuranceNetworksQuery(params, options) {
	return useQuery(['InsuranceNetworks', params], () => fetch(params), {
		staleTime: 0,
		...options
	})
}

export default useInsuranceNetworksQuery
