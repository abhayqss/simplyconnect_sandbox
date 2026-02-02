import { useQuery } from 'hooks/common/redux'

import actions from 'redux/incident/report/community/list/communityListActions'

import { isInteger } from 'lib/utils/Utils'

export default function useCommunitiesQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.organizationId)
            && params.organizationId !== prevParams.organizationId
        ),
        ...options
    })
}