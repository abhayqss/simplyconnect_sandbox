
import { useListQuery } from 'hooks/common'
import service from 'services/AppointmentService'

function fetch(params) {
    return service.findAppointmentHistory(params)
}

function useAppointmentHistoryQuery(params, options) {
    return useListQuery('Appointment.History', { size: 15, ...params },
        { fetch, staleTime: 0, ...options })
}

export default useAppointmentHistoryQuery
