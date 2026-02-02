import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findAppointmentNotificationMethods(params)

export default function useAppointmentNotificationMethodsQuery(params, options) {
    return useQuery(['Directory.AppointmentNotificationMethods', params], () => fetch(params), options)
}
