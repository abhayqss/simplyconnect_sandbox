import { useRef, useEffect } from 'react'

import { noop } from 'underscore'

import { useRefCurrent } from 'hooks/common/index'

function useMutationWatch(target, handle = noop, trigger = prev => target !== prev) {
    const prevRef = useRef(target)
    const options = useRefCurrent({ handle, trigger })

    useEffect(() => {
        if (options.trigger(prevRef.current)) {
            options.handle(prevRef.current)
            prevRef.current = target
        }
    }, [target, prevRef, options])
}

export default useMutationWatch
