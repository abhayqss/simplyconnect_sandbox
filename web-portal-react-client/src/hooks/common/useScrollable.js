import React, {
    useRef,
    useMemo,
    useCallback
} from 'react'

import BaseScrollable from 'components/Scrollable/Scrollable'

function useScrollable() {
    const scrollableRef = useRef()
    const Scrollable = useMemo(() => ({ children, ...restProps }) => (
        <BaseScrollable ref={scrollableRef} {...restProps}>
            {children}
        </BaseScrollable>
    ), [scrollableRef])

    const scroll = useCallback((...args) => {
        scrollableRef.current.scroll(...args)
    }, [])

    return { scroll, Scrollable }
}

export default useScrollable
