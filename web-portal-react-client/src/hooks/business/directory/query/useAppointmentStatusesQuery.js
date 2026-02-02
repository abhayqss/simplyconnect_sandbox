import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findAppointmentStatuses(params)

export default function useAppointmentStatusesQuery(params, options) {
    return useQuery(['Directory.AppointmentStatuses', params], () => fetch(params), options)
}
