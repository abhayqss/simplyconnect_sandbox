import { useQuery } from '@tanstack/react-query'

import service from 'services/AuditLogService'

const fetch = params => service.findDateOfOldest(params)

function useOldestAuditLogDateQuery(params, options) {
    return useQuery(['OldestAuditLogDate', params], () => fetch(params), options)
}

export default useOldestAuditLogDateQuery
