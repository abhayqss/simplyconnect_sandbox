import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqInCommunityValidator from 'validators/UniqInCommunityValidator'

export default function useUniqInCommunityValidation({
    clientId,
    fieldName,
    communityId,
    memberNumber,
    medicareNumber,
    medicaidNumber,
}) {
    const [validate, error] = useValidation(UniqInCommunityValidator)

    const onValidate = useCallback(
        () => validate({
            clientId,
            communityId,
            memberNumber,
            medicareNumber,
            medicaidNumber,
        }, { field: fieldName }),
        [
            validate,
            clientId,
            communityId,
            memberNumber,
            medicareNumber,
            medicaidNumber,
            fieldName
        ]
    )

    return [onValidate, error]
}