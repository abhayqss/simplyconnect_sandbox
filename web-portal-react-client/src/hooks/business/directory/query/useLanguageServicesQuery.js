import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = () => service.findLanguageServices()

function useLanguageServicesQuery(params, options) {
    return useQuery('LanguageServices', params, {
        fetch,
        ...options,
    })
}

export default useLanguageServicesQuery
