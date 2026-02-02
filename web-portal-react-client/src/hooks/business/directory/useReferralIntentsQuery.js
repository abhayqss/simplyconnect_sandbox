import { useEffect } from 'react'

import { useDirectoryData } from 'hooks/common'

import useBoundActions from '../../common/redux/useBoundActions'

import actions from 'redux/directory/referral/intent/list/referralIntentListActions'

export default function useReferralIntentsQuery(shouldReload = false) {
    const { intents } = useDirectoryData({
        intents: ['referral', 'intent']
    })

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (!intents.length || shouldReload) {
            load()
        }
    }, [load, intents.length, shouldReload])
}
