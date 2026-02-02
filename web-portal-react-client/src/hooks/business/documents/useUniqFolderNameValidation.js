import { useCallback } from 'react'

import { useValidation } from 'hooks/common'

import UniqFolderNameValidator from 'validators/UniqFolderNameValidator'

function useUniqFolderNameValidation({ id, name, parentId, communityId }) {
    const [validate, error] = useValidation(UniqFolderNameValidator)

    const onValidate = useCallback(
        () => validate({ id, name, parentId, communityId }, {
            field: name,
        }),
        [id, name, parentId, communityId, validate]
    )

    return [onValidate, error]
}

export default useUniqFolderNameValidation
