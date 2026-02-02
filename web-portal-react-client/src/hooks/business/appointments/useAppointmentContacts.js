import { useQuery } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function fetch(params) {
	return service.findContacts(params)
}

function useAppointmentContacts(params, options) {
	return useQuery(['AppointmentContacts', params], () => fetch(params), options)
}

export default useAppointmentContacts
