import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findReferralCategories(params)

function useReferralCategoriesQuery(params, options) {
    return useQuery(['Directory.ReferralCategories', params], () => fetch(params), options)
}

export default useReferralCategoriesQuery
