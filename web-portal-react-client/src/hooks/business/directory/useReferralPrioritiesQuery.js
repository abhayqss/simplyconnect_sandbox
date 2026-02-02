import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/referral/priority/list/referralPriorityListActions'

export default function useReferralPrioritiesQuery(params, options) {
    useQuery(actions, params, options)
}
