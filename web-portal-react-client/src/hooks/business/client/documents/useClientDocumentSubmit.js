import { useMutation } from 'hooks/common'

import service from 'services/ClientDocumentService'

function submit({ clientId, ...data }) {
    return service.save(data, { clientId })
}

function useClientDocumentSubmit(params, options) {
    return useMutation(params, submit, options)
}

export default useClientDocumentSubmit
