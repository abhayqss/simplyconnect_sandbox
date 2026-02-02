import {
    useRef,
    useEffect,
    useCallback
} from 'react'

export default function useLocalCache(key) {
    const ref = useRef(new Map())

    const get = useCallback((k = key) => {
        return ref.current.get(k)
    }, [key])

    const update = useCallback(changes => {
        ref.current.set(key, { ...get(key), ...changes })
    }, [key, get])

    useEffect(() => () => {
        ref.current.clear()
    }, [])

    return { get, update }
}