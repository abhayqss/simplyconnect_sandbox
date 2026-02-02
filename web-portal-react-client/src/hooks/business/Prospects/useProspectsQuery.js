import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/ProspectService'

const fetch = params => service.find(params)

export default function useProspectsQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}