import { useQuery } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function fetch(params) {
    return service.count(params)
}

function useAppointmentCountQuery(params, options) {
    return useQuery(['AppointmentCount', params], () => fetch(params), options)
}

export default useAppointmentCountQuery
