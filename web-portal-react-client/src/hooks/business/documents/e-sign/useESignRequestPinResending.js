import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const mutate = ({ requestId, ...params }) => service.resendSignatureRequestPin({ requestId, ...params })

function useESignRequestPinResending(options) {
    return useMutation(mutate, options)
}

export default useESignRequestPinResending
