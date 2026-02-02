import { useQuery } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function fetch(params) {
	return service.findParticipation(params)
}

function useAppointmentParticipationQuery(params, options) {
	return useQuery(['Appointment.Participation', params], () => fetch(params), options)
}

export default useAppointmentParticipationQuery
