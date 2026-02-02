import * as actions from 'redux/client/servicePlan/count/servicePlanCountActions'

import useQuery from './useQuery'
import useClear from '../../common/redux/useClear'

export default function useAssessmentCountQuery(params, options) {
    useClear(actions)
    useQuery(actions, params, options)
}