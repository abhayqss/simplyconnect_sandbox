import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findAttorneyTypes(params)

function useAttorneyTypesQuery(params, options) {
    return useQuery(['Directory.AttorneyPowerTypes', params], () => fetch(params), options)
}

export default useAttorneyTypesQuery
