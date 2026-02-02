import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const mutate = ({ bulkRequestId, ...params }) => service.renewBulkRequestById(bulkRequestId, params)

function useESignBulkRequestRenewal(options) {
    return useMutation(mutate, options)
}

export default useESignBulkRequestRenewal
