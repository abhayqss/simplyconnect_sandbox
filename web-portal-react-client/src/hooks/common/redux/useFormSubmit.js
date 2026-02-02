import { useCallback } from 'react'
import useRefCurrent from '../useRefCurrent'

const defaultValidation = { isEnabled: true }

function useFormSubmit(state, actions, params, { validation, shouldPreventIfNotChanged = true }) {
    params = useRefCurrent(params)
    validation = useRefCurrent({ ...defaultValidation, ...validation })

    return useCallback(async function submitCb(data) {
        let isValid = true

        if (validation.isEnabled) {
            isValid = await actions.validate(
                state.fields.toJS(), validation.options
            )
        }

        let response = null

        let isChanged = (
            !shouldPreventIfNotChanged
            || state.isChanged()
        )

        if (isValid && isChanged) {
            try {
                response = await actions.submit(
                    data, params
                )
            } catch (error) {
                return { error }
            }
        }

        return response
    }, [actions, params, shouldPreventIfNotChanged, state, validation])
}

export default useFormSubmit