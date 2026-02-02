import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = () => service.findAdditionalServices()

function useAdditionalServicesQuery(params, options) {
    return useQuery('AdditionalServices', params, {
        fetch,
        ...options,
    })
}

export default useAdditionalServicesQuery
