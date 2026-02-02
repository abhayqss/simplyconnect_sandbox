import { useCallback } from 'react'

import $ from 'jquery'
import { noop } from 'underscore'

function useScrollToFormError(
    formSelector,
    scroll = noop,
    {
        index = 0,
        duration = 300,
        offset = -50,
        shouldFocus = true
    } = {}
) {
    return useCallback(() => {
        const target = (
            $(formSelector)
                .find('.is-invalid')
                .get(index) || null
        )

        if (shouldFocus && target) {
            target.focus()
        }

        if (shouldFocus && !target) {
            console.log(`'${formSelector} .is-invalid' selector has not been found`)
        }

        scroll(target, duration, { offset })
    }, [
        index,
        scroll,
        offset,
        duration,
        shouldFocus,
        formSelector
    ])
}

export default useScrollToFormError