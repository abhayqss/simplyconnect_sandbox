import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/referral/status/list/referralStatusListActions'

export default function useReferralStatusesQuery(params, options) {
    useQuery(actions, params, options)
}
