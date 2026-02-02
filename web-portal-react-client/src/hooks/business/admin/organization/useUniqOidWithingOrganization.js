import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqWithinOrganizationValidator from 'validators/UniqWithinOrganizationValidator'

export default function useUniqOidWithingOrganization(oid) {
    const [validate, error] = useValidation(UniqWithinOrganizationValidator)

    const onValidate = useCallback(
        () => validate({ oid }, {
            fieldToValidate: 'oid',
            errorMessage: 'Entered OID already exists in the system.'
        }),
        [oid, validate]
    )

    return [onValidate, error]
}