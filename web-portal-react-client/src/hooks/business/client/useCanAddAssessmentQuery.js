import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/client/assessment/can/add/canAddAssessmentActions'

export default function useCanAddAssessmentQuery(params, options) {
    return useQuery(actions, params, options)
}