import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/DocumentFolderAggregatedService'

const fetch = params => service.find(params)

export default function useDocumentsQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}