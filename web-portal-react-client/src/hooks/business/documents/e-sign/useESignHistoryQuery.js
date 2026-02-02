import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/DocumentESignService'

const fetch = params => service.findSignatureHistory(params)

export default function useESignHistoryQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, () => fetch(params), options)
}