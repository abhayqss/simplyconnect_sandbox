import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = () => service.findEmergencyServices()

function useEmergencyServicesQuery(params, options) {
    return useQuery('EmergencyServices', params, {
        fetch,
        ...options,
    })
}

export default useEmergencyServicesQuery
