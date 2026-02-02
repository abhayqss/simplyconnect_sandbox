import { useQuery } from '@tanstack/react-query'

import service from 'services/CommunityService'

const fetch = params => service.findTreatmentServices(params)

function useTreatmentServicesQuery(params, options) {
    return useQuery(['Community.TreatmentService', params], () => fetch(params), options)
}

export default useTreatmentServicesQuery
