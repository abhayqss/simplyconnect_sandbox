import { useQuery } from '@tanstack/react-query'

import service from 'services/ServicePlanService'

const fetch = params => service.count(params, {
    response: { extractDataOnly: true }
})

function useClientServicePlanCountQuery(params, options) {
    return useQuery(['Client.ServicePlan.Count', params], () => fetch(params), options)
}

export default useClientServicePlanCountQuery