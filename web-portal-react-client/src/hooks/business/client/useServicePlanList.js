import useList from 'hooks/common/useList'

import service from 'services/ServicePlanService'

export default function useServicePlanList(params) {
    return useList('SERVICE_PLAN', params, {
        doLoad: params => service.find(params)
    })
}
