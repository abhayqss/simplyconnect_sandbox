import { useEffect } from 'react'

import { useDirectoryData } from 'hooks/common'

import useBoundActions from '../../common/redux/useBoundActions'

import actions from 'redux/directory/referral/reason/list/referralReasonListActions'

export default function useReferralReasonsQuery(shouldReload = false) {
    const { reasons } = useDirectoryData({
        reasons: ['referral', 'reason']
    })

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (!reasons.length || shouldReload) {
            load()
        }
    }, [load, shouldReload, reasons.length])
}
