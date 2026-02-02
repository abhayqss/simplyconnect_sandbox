import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/ClientDocumentService'

const fetch = params => service.find(params)

export default function useClientDocumentsQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}