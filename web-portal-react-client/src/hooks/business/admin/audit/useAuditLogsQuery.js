import { useListQuery } from 'hooks/common'

import service from 'services/AuditLogService'

const fetch = params => service.find(params)

function useAuditLogsQuery(params, options) {
    return useListQuery(
        'AuditLogs',
        { size: 15, ...params },
        {
            fetch,
            ...options,
            cacheTime: 0,
            staleTime: 0
        }
    )
}

export default useAuditLogsQuery
