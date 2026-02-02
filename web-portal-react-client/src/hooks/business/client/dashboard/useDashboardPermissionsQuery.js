import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDashboardService'

const fetch = params => service.findDashboartPermissions(params)

function useDashboardPermissionsQuery(params, options) {
    return useQuery(['Client.DashboardPermissions', params], () => fetch(params), options)
}

export default useDashboardPermissionsQuery