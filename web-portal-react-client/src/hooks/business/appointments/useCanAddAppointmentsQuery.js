import { useQuery } from '@tanstack/react-query'

import service from 'services/AppointmentService'

const fetch = params => service.canAdd(
    params, { response: { extractDataOnly: true } }
)

export default function useCanAddAppointmentsQuery(params, options) {
    return useQuery(['Appointments.CanAdd', params], () => fetch(params), options)
}