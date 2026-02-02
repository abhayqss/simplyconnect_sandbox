import * as actions from 'redux/directory/assessment/type/list/assessmentTypeListActions'

import useClear from 'hooks/common/redux/useClear'
import useQuery from 'hooks/common/redux/useQuery'

export default function useAssessmentTypesQuery(params, options) {
    useClear(actions)
    useQuery(actions, params, options)
}