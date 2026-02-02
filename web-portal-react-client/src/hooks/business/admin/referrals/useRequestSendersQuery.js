import { useQuery } from 'hooks/common/redux'

import actions from 'redux/referral/request/sender/list/referralRequestSenderListActions'

import { isInteger } from 'lib/utils/Utils'

export default function useRequestSendersQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.organizationId)
            && (
                params.organizationId !== prevParams.organizationId
                || params.communityIds !== prevParams.communityIds
            )
        ),
        ...options
    })
}