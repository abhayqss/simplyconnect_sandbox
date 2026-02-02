import {
    useMemo,
    useState,
    useCallback
} from 'react'

import {
    noop,
    isFunction
} from 'underscore'

function useValidation(Validator) {
    const [errors, setErrors] = useState({})

    if (Validator && !isFunction(Validator)) {
        throw new Error('Validator should be a constructor function in useValidation hook')
    }

    const validator = useMemo(
        () => Validator ? new Validator() : null, [Validator]
    )

    function onSuccessValidation() {
        setErrors({})
    }

    function onFailureValidation(errors) {
        setErrors(errors)
        throw errors
    }

    function hook_validate(data, options) {
        return validator
            .validate(data, options)
            .then(onSuccessValidation)
            .catch(onFailureValidation)
    }

    const validate = useCallback(hook_validate, [validator])

    return validator ? [validate, errors, setErrors] : [noop, errors, setErrors]
}

export default useValidation
