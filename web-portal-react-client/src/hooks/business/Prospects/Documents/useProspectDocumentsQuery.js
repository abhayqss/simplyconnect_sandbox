import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/ProspectDocumentService'

const fetch = params => service.find(params)

export default function useProspectDocumentsQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}