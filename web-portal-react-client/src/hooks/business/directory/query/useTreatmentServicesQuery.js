import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findTreatmentServices(params)

function useTreatmentServicesQuery(params, options) {
    return useQuery('TreatmentServices', params, {
        fetch,
        ...options
    })
}

export default useTreatmentServicesQuery
