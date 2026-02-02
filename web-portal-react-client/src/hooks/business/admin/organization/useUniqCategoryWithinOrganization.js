import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqOrganizationCategoryValidator from 'validators/UniqOrganizationCategoryValidator'

export default function useUniqCategoryWithinOrganization({ name, categoryId, organizationId }) {
    const [validate, error] = useValidation(UniqOrganizationCategoryValidator)

    const onValidate = useCallback(
        () => validate({ name, categoryId, organizationId }, {
            fieldToValidate: 'name',
        }),
        [name, categoryId, organizationId, validate]
    )

    return [onValidate, error]
}