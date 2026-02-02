import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqWithinOrganizationValidator from 'validators/UniqWithinOrganizationValidator'

export default function useUniqNameWithinOrganization(name, id) {
    const [validate, error] = useValidation(UniqWithinOrganizationValidator)

    const onValidate = useCallback(
        () => validate({ name, organizationId: id }, {
            fieldToValidate: 'name',
            errorMessage: 'The organization with name entered already exists.'
        }),
        [id, name, validate]
    )

    return [onValidate, error]
}