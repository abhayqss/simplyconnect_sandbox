import { useQuery } from '@tanstack/react-query'

import service from 'services/ServicePlanService'

const fetch = params => service.canView(params, {
	response: { extractDataOnly: true }
})

function useCanViewClientServicePlansQuery(params, options) {
	return useQuery(['Client.ServicePlan.CanView', params], () => fetch(params), options)
}

export default useCanViewClientServicePlansQuery