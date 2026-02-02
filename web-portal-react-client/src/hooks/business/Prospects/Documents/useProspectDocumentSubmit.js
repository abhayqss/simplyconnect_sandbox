import { useCallback } from 'react'
import { useMutation } from '@tanstack/react-query'

import service from 'services/ProspectDocumentService'

function submit({ prospectId, ...data }) {
    return service.save(data, { prospectId })
}

function useProspectDocumentSubmit(prospectId, options) {
    const [_mutate, result] = useMutation(submit, options)

    const mutate = useCallback(
        data => _mutate({ prospectId, ...data }),
        [prospectId, _mutate]
    )

    return [mutate, result]
}

export default useProspectDocumentSubmit
