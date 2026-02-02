import { useQuery } from 'hooks/common/redux'

import { isInteger } from 'lib/utils/Utils'

import actions from 'redux/event/community/list/communityListActions'

export default function useCommunitiesQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.organizationId)
            && params.organizationId !== prevParams.organizationId
        ),
        ...options
    })
}