import { useState, useEffect } from 'react'

import { useLocation, useRouteMatch } from 'react-router-dom'

import { useMutationWatch } from './'

import { path } from 'lib/utils/ContextUtils'

function useNestedRoutesTracking(route, { onRouteChanged }) {
    const [isWatching, setIsWatching] = useState(false)

    const location = useLocation()
    const isMatched = !!useRouteMatch(path(route))

    useMutationWatch(location, function watch() {
        if (isWatching) {
            onRouteChanged(location)
        }
    })

    useEffect(function matchCheck() {
        if (isMatched) {
            setIsWatching(true)

            return () => {
                setIsWatching(false)
            }
        }
    }, [isMatched])
}

export default useNestedRoutesTracking