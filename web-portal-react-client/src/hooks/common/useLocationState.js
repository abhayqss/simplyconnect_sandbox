import { useState, useCallback } from 'react'

import {
    useHistory,
    useLocation
} from 'react-router-dom'

function useLocationState({ isCached = true } = {}) {
    const history = useHistory()
    const location = useLocation()

    const [state] = useState(location.state)

    const clear = useCallback(() => state && history.replace({}), [history, state])

    return [isCached ? state : location.state, clear]
}

export default useLocationState
