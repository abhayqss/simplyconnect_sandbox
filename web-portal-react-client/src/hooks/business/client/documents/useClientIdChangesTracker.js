import { useRef, useEffect } from 'react'

import { useSelector } from 'react-redux'

import { useBoundActions } from 'hooks/common/redux'

import clientLastViewedActions from 'redux/client/lastViewed/clientLastViewedActions'

function useClientIdChangesTracker(clientId, onClientIdChange) {
    const callback = useRef()

    const setId = useBoundActions(clientLastViewedActions.setId)

    const lastViewedId = useSelector(state => state.client.lastViewed.id)

    useEffect(() => {
        callback.current = onClientIdChange
    })

    useEffect(() => {
        setId(clientId)
    }, [setId, clientId])

    useEffect(() => {
        if (lastViewedId !== clientId) {
            callback.current(lastViewedId)
        }
    }, [clientId, lastViewedId])
}

export default useClientIdChangesTracker
