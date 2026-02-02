import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = (params) => service.findGroupedEventTypes(
	params, { response: { extractDataOnly: true } }
)

export default function useEventTypesQuery(params, options) {
	return useQuery(['Directory.EventTypes', params], () => fetch(params), options)
}
