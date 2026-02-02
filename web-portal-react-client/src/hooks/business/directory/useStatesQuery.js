import { useEffect } from 'react'

import { useDirectoryData } from 'hooks/common'

import useBoundActions from '../../common/redux/useBoundActions'

import actions from 'redux/directory/state/list/stateListActions'

export default function useStatesQuery(shouldReload = false) {
    const { states } = useDirectoryData({
        states: ['state']
    })

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (!states.length || shouldReload) {
            load()
        }
    }, [load, states.length, shouldReload])
}
