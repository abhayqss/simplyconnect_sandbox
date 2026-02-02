import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqSSNInCommunityValidator from 'validators/UniqSSNInCommunityValidator'

export default function useUniqSSnInCommunityValidation({
    ssn,
    clientId,
    communityId,
}) {
    const [validate, error] = useValidation(UniqSSNInCommunityValidator)

    const onValidate = useCallback(
        () => validate({
            ssn,
            clientId,
            communityId,
        }),
        [validate, ssn, clientId, communityId]
    )

    return [onValidate, error]
}