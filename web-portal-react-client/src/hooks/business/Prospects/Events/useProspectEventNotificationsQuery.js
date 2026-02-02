import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/ProspectEventService'

const fetch = params => service.findEventNotifications(params)

export default function useProspectEventNotificationsQuery(params, options) {
	return useManualPaginatedQuery(params, fetch, options)
}