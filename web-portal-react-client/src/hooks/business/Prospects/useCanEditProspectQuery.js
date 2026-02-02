import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectService'

const fetch = (params) => service.canEdit(params)

export default function useCanEditProspectQuery(params, options) {
    return useQuery(['CanEditProspect', params], () => fetch(params), options)
}