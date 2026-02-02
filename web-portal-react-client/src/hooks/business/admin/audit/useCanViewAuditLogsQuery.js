import { useQuery } from '@tanstack/react-query'

import service from 'services/AuditLogService'

const fetch = params => service.canView(params)

function useCanViewAuditLogsQuery(params, options) {
    return useQuery(['CanViewAuditLogs', params], () => fetch(params), options)
}

export default useCanViewAuditLogsQuery
