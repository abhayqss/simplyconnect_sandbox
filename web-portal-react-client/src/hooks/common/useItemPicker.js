import {
    useCallback
} from 'react'

import { reject } from 'underscore'

import { useRefCurrent } from 'hooks/common/index'

export default function useItemPicker(picked, {
    onPicked,
    isEqual = (a, b) => a === b
}) {
    const params = useRefCurrent({ isEqual, onPicked })

    const pick = useCallback(item => {
        params.onPicked([...picked, item])
    }, [picked, params])

    const unpick = useCallback(item => {
        params.onPicked(reject(picked, o => params.isEqual(o, item)))
    }, [picked, params])

    const pickAll = useCallback(items => {
        params.onPicked(items)
    }, [params])

    const unpickAll = useCallback(() => {
        params.onPicked([])
    }, [params])

    return { pick, unpick, pickAll, unpickAll }
}