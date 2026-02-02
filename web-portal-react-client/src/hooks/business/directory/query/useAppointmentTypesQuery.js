import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findAppointmentTypes(params)

export default function useAppointmentTypesQuery(params, options) {
    return useQuery(['Directory.AppointmentTypes', params], () => fetch(params), options)
}
