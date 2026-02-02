import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqEmailValidator from 'validators/UniqEmailValidator'

export default function useUniqEmailValidation({ email, clientId, organizationId }) {
    const [validate, error] = useValidation(UniqEmailValidator)

    const onValidate = useCallback(
        () => validate({ email, clientId, organizationId }),
        [clientId, email, organizationId, validate]
    )

    return [onValidate, error]
}