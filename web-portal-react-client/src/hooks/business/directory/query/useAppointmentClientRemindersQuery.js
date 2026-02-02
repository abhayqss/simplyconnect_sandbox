import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findAppointmentClientReminders(params)

export default function useAppointmentClientRemindersQuery(params, options) {
    return useQuery(['Directory.AppointmentClientReminders', params], () => fetch(params), options)
}
