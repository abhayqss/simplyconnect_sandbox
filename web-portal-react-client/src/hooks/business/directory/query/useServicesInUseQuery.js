import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findServiceInUse(params)

function useServicesInUseQuery(params, options) {
    return useQuery(['Directory.ServicesInUse', params], () => fetch(params), options)
}

export default useServicesInUseQuery