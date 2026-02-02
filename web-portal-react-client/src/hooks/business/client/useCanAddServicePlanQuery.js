import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/client/servicePlan/can/add/canAddServicePlanActions'

export default function useCanAddServicePlanQuery(params, options) {
    return useQuery(actions, params, options)
}