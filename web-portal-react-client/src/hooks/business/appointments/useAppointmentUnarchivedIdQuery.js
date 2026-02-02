import { useQuery } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function fetch(params) {
    return service.findAppointmentUnarchivedId(params)
}

function useAppointmentUnarchivedIdQuery(params, options) {
    return useQuery(['AppointmentUnarchivedId', params], () => fetch(params), options)
}

export default useAppointmentUnarchivedIdQuery
