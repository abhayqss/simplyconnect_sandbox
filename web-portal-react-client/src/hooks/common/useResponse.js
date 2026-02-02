import { useCallback } from 'react'

import { noop } from 'underscore'

import WebError from 'lib/errors/WebError'

function  useResponse({ onSuccess = noop, onFailure = noop, onUnknown = noop }) {
    return useCallback(response => {
        const { data, success, error, ...rest } = response || {}

        if (success) {
            return onSuccess({ data, ...rest })
        } else if (error) {
            return onFailure(error)
        } else if (response instanceof WebError) {
            return onFailure(response)
        } else {
            const { success, error } = rest?.body || {}

            return onUnknown(!success && (error || response))
        }
    }, [onFailure, onSuccess, onUnknown])
}

export default useResponse