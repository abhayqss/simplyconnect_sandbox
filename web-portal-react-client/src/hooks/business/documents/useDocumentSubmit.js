import { useMutation } from 'hooks/common'

import service from 'services/DocumentService'

function submit(data) {
    return service.save(data)
}

function useDocumentSubmit(params, options) {
    return useMutation(params, submit, options)
}

export default useDocumentSubmit
