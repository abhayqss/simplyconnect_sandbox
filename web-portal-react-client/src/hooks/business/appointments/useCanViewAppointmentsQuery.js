import { useQuery } from '@tanstack/react-query'

import service from 'services/AppointmentService'

const fetch = params => service.canView(
    params, { response: { extractDataOnly: true } }
)

export default function useCanViewAppointmentsQuery(params, options) {
    return useQuery(['Appointments.CanView', params], () => fetch(params), options)
}