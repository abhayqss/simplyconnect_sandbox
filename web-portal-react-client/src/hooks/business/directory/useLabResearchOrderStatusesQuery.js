import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/lab/research/order/status/list/labResearchOrderStatusListActions'

import { isInteger } from 'lib/utils/Utils'

export default function useLabResearchOrderStatusesQuery(params, options) {
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
