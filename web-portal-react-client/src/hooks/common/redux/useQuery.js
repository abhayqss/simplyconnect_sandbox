import { useRef, useEffect, useCallback } from 'react'

import { noop, isEqual } from 'underscore'

import { useRefCurrent } from 'hooks/common/index'
import { useBoundActions } from 'hooks/common/redux/index'

import { Response } from 'lib/utils/AjaxUtils'

export default function useQuery(
    actions,
    params = {},
    {
        condition = (prevParams) => !isEqual(params, prevParams),
        onSuccess = noop,
        onFailure = noop,
        onUnknown = noop
    } = {}
) {
    const options = useRefCurrent({
        condition,
        onSuccess,
        onFailure,
        onUnknown
    })

    const prevParamsRef = useRef({})

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (options.condition(prevParamsRef.current)) {
            load(params).then(Response(
                options.onSuccess,
                options.onFailure,
                options.onUnknown
            ))
        }

        prevParamsRef.current = params
    }, [ load, params, prevParamsRef, options ])

    return {
        fetch: useCallback(() => (
            load(params).then(Response(
                options.onSuccess,
                options.onFailure,
                options.onUnknown
            ))
        ), [load, options, params])
    }
}