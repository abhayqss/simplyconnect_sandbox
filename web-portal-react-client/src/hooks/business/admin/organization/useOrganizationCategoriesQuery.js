import { useManualPaginatedQuery } from 'hooks/common'

import service from 'services/CategoryService'

const fetch = params => service.find(params)

function useOrganizationCategoriesQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}

export default useOrganizationCategoriesQuery