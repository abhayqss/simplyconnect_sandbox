import { useQuery } from 'hooks/common/redux'

import actions from 'redux/referral/request/can/add/canAddReferralRequestActions'

import { isInteger } from 'lib/utils/Utils'

export default function useCanAddReferralRequestQuery(params, options) {
    useQuery(actions, params , {
        condition: prevParams => (
            isInteger(params.communityId)
            && params.communityId !== prevParams.communityId
        ),
        ...options
    })
}