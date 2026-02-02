import {
    useState,
    useEffect,
    useCallback
} from 'react'

import { noop } from 'underscore'

import { useDebounce } from 'use-debounce'

import { useResponse } from 'hooks/common'

function useNoteFormBehaviour(
    state,
    { submit = noop, validate = noop, scrollToError = noop },
    { onSuccess = noop, onFailure = noop, onCancel = noop }
) {
    const isValid = state.isValid()

    const [response, setResponse] = useState(null)

    const [debouncedFields] = useDebounce(state.fields, 200)

    const onSubmitSuccess = useCallback(() => {
        setResponse(null)
        onSuccess(response)
    }, [onSuccess, response])

    const onResponse = useResponse({
        onFailure,
        onSuccess: onSubmitSuccess
    })

    const onSubmit = useCallback((e) => {
        e.preventDefault()
        submit().then(resp => {
            if (resp) {
                setResponse(resp)
                onResponse(resp)
            } else scrollToError()
        }).catch(error => {
            onResponse({ error })
        })
    }, [submit, onResponse, scrollToError])

    useEffect(() => {
        if (!isValid) {
            validate(debouncedFields.toJS())
        }
    }, [validate, isValid, debouncedFields])

    return { onSubmit }
}

export default useNoteFormBehaviour
