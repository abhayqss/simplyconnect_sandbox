import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectService'

const fetch = (params) => service.canAdd(params)

export default function useCanAddProspectQuery(params, options) {
    return useQuery(['CanAddProspect', params], () => fetch(params), options)
}