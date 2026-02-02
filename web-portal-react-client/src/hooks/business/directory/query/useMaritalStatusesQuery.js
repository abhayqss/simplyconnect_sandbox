import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = (params) => service.findMaritalStatus(params, {
	response: { extractDataOnly: true }
})

function useMaritalStatusesQuery(params, options) {
	return useQuery(['Directory.MaritalStatuses', params], () => fetch(params), options)
}

export default useMaritalStatusesQuery