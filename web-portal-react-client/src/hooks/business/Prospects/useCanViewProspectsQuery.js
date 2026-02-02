import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectService'

const fetch = (params) => service.canView(params)

export default function useCanViewProspectsQuery(params, options) {
    return useQuery(['CanViewProspects', params], () => fetch(params), options)
}