import { useEffect } from 'react'

import useBoundActions from '../../common/redux/useBoundActions'

import actions from 'redux/directory/referral/decline/reason/list/referralDeclineReasonListActions'

export default function useReferralDeclineReasonsQuery() {
    const load = useBoundActions(actions.load)

    useEffect(() => { load() }, [load])
}
