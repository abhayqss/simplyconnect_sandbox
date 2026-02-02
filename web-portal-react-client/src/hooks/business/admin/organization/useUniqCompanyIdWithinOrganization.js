import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqWithinOrganizationValidator from 'validators/UniqWithinOrganizationValidator'

export default function useUniqCompanyIdWithinOrganization(companyId) {
    const [validate, error] = useValidation(UniqWithinOrganizationValidator)

    const onValidate = useCallback(
        () => validate({ companyId }, {
            fieldToValidate: 'companyId',
            errorMessage: 'The organization with Company ID entered already exists in the system.'
        }),
        [companyId, validate]
    )

    return [onValidate, error]
}