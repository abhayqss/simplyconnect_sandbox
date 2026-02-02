import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/lab/research/reason/list/labResearchReasonListActions'

import { isInteger } from 'lib/utils/Utils'

export default function useLabResearchReasonsQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => {
            return (
                isInteger(params.organizationId)
                && params.organizationId !== prevParams.organizationId
            )
        },
        ...options
    })
}
