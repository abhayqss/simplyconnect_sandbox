import { useQuery } from '@tanstack/react-query'

import service from 'services/CategoryService'

const fetch = params => service.canAdd(params)

function useCanAddOrganizationCategoryQuery(params, options) {
    return useQuery(['CanAddOrganizationCategories', params], () => fetch(params), options)
}

export default useCanAddOrganizationCategoryQuery