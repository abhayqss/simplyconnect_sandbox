import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const mutate = ({ requestId, ...params }) => service.renewSignatureRequestById(requestId, params)

function useESignRequestRenewal(options) {
    return useMutation(mutate, options)
}

export default useESignRequestRenewal
