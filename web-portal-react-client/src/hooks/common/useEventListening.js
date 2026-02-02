import { useEffect } from 'react'

import {
    noop,
    isFunction
} from 'lib/utils/FuncUtils'

export default function useEventListening(target, type = 'click', callback = noop) {
    useEffect(() => {
        if (target instanceof Element && isFunction(callback)) {
            target.addEventListener(type, callback)
            return () => target.removeEventListener(type, callback)
        }
    }, [target, type, callback])
}