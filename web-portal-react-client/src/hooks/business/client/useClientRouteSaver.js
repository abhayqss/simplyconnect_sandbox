import { useEffect } from 'react'

import { useHistory } from 'react-router-dom'

import { useNestedRoutesTracking } from 'hooks/common'
import { useBoundActions } from 'hooks/common/redux'

import actions from 'redux/client/route/clientRouteActions'

function useClientRouteSaver() {
    const history = useHistory()

    const { change } = useBoundActions(actions)

    useNestedRoutesTracking('/clients', {
        onRouteChanged: (location) => {
            change(location.pathname)
        }
    })

    useEffect(function initialChange() {
        change(history.location.pathname)
    }, [change, history])
}

export default useClientRouteSaver
