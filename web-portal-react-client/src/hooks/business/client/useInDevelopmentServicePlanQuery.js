import { useQuery } from 'hooks/common/redux'

import actions from 'redux/client/dashboard/servicePlan/details/clientServicePlanDetailsActions'

export default function useInDevelopmentServicePlanQuery(params, options) {
    useQuery(actions, params, options)
}
