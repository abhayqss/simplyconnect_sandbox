import { useRef, useCallback } from 'react'

function useAbortable(request) {
    const controllerRef = useRef(null)

    const abort = useCallback(() => {
        if (controllerRef.current) {
            controllerRef.current.abort()
        }
    }, [])

    const abortableRequest = useCallback(() => {
        abort()

        let isAborted = false

        controllerRef.current = new AbortController()

        return new Promise((resolve) => {
            controllerRef.current.signal.onabort = () => {
                isAborted = true
                resolve(null)
            }

            request().then(result => resolve(isAborted ? null : result))
        }, [])
    }, [abort, request])

    return [abortableRequest, abort]
}

export default useAbortable