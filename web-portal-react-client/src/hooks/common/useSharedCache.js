import {
    useMemo,
    useEffect,
    useCallback
} from 'react'

import {
    stableStringify
} from 'lib/utils/Utils'

import {
    isString
} from 'lib/utils/StringUtils'

const cache = new Map()

function stringifyKey(key) {
    return isString(key) ? key : stableStringify(key)
}

export default function useSharedCache(key) {
    const stringKey = useMemo(
        () => stringifyKey(key), [key]
    )

    const get = useCallback(k => {
        return cache.get(k ? stringifyKey(k) : stringKey)
    }, [stringKey])

    const update = useCallback(changes => {
        cache.set(stringKey, { ...get(), ...changes })
    }, [stringKey, get])

    const clear = useCallback(() => {
        cache.clear()
    }, [])

    const remove = useCallback((k = stringKey) => {
        cache.delete(k)
    }, [stringKey])

    return useMemo(() => ({
        get,
        clear,
        update,
        remove,
        stringKey,
        stringifyKey
    }), [
        get,
        update,
        remove,
        clear,
        stringKey
    ])
}