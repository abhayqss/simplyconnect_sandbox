import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findTreatmentServicesInUse(params)

function useTreatmentServicesInUseQuery(params, options) {
    return useQuery(['Directory.TreatmentServicesInUse', params], () => fetch(params), options)
}

export default useTreatmentServicesInUseQuery