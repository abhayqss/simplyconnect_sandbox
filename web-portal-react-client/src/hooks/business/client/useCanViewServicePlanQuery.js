import * as actions from 'redux/client/servicePlan/can/view/canViewServicePlanActions'

import useQuery from './useQuery'

export default function useCanViewServicePlanQuery(params, options) {
    return useQuery(actions, params, options)
}