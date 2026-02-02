import { useQuery } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function fetch({ id }) {
    return service.findById(id)
}

function useAppointmentQuery(params, options) {
    return useQuery(['Appointment', params], () => fetch(params), options)
}

export default useAppointmentQuery
