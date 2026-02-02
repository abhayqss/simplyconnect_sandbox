import { useMutation } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function useAppointmentSubmit(options) {
    return useMutation(data => service.save(data), options)
}

export default useAppointmentSubmit
