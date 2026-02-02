import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findMarketplaceLanguages(params)

function useMarketplaceLanguagesQuery(params, options) {
    return useQuery(['Directory.MarketplaceLanguages', params], () => fetch(params), options)
}

export default useMarketplaceLanguagesQuery
