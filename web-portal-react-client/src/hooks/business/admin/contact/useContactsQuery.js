import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/ContactService'

const fetch = params => service.find(params)

export default function useContactsQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}