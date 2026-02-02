import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const mutate = ({ bulkRequestId, ...params }) => service.cancelBulkRequestById(bulkRequestId, params)

function useESignBulkRequestCancel(options) {
    return useMutation(mutate, options)
}

export default useESignBulkRequestCancel
