import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const mutate = ({ requestId }) => service.cancelSignatureRequestById(requestId)

function useESignRequestCancel(options) {
    return useMutation(mutate, options)
}

export default useESignRequestCancel
