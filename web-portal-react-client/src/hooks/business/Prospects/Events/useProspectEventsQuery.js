import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/ProspectEventService'

const fetch = params => service.find(params)

export default function useProspectEventsQuery(params, options) {
	return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}