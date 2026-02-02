import {
    useRef,
    useMemo,
    useEffect,
    useCallback
} from 'react'

import { noop } from 'underscore'

import { useRefCurrent } from 'hooks/common'

export default function useMutationObserver(target, handle = noop, condition = prev => target !== prev) {
    const prevRef = useRef(target)
    const options = useRefCurrent({ handle, condition, isEnabled: true })

    const enable = useCallback(() => options.isEnabled = true, [options])
    const disable = useCallback(() => options.isEnabled = false, [options])

    useEffect(() => {
        if (options.condition(prevRef.current)) {
            options.isEnabled && options.handle(prevRef.current)
            prevRef.current = target
        }
    }, [target, prevRef, options])

    return useMemo(() => ({
        enable,
        disable,
        isEnabled: options.isEnabled
    }), [options, enable, disable])
}
