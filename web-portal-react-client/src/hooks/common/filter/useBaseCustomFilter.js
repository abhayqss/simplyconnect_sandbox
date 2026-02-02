import { useEffect, useCallback, useState } from 'react'

import { noop } from 'underscore'

import { useRefCurrent } from 'hooks/common/index'

import { useFilter } from './index'

export default function useBaseCustomFilter(
    name,
    data,
    {
        onClear = noop,
        onReset = noop,
        onApply = noop,
        onRestore = noop,
        onPressEnterKey
    }
) {
    const [isFocused, setIsFocused] = useState(false)

    const options = useRefCurrent({
        onReset, onPressEnterKey
    })

    const {
        save,
        apply,
        remove,
        restore,
        isSaved
    } = useFilter(name, data, {
        onClear, onApply, onRestore
    })

    const focus = useCallback(() => {
        setIsFocused(true)
    }, [])

    const blur = useCallback(() => {
        setIsFocused(false)
    }, [])

    const reset = useCallback((data, shouldReReset) => {
        options.onReset(data, shouldReReset)
    }, [ options ])

    const onKeyUp = useCallback(({ keyCode }) => {
        if (isFocused && keyCode === 13) {
            if (options.onPressEnterKey) {
                options.onPressEnterKey()
            } else apply()
        }
    }, [apply, isFocused, options])

    useEffect(() => {
        window.addEventListener('keyup', onKeyUp)

        return () => {
            window.removeEventListener('keyup', onKeyUp)
        }
    }, [ onKeyUp ])

    return {
        blur,
        focus,
        apply,
        reset,
        save,
        remove,
        restore,
        isSaved
    }
}