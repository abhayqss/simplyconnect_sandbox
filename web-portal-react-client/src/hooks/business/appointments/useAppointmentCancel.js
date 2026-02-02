import { useMutation } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function useAppointmentCancel(options) {
    return useMutation(data => service.cancel(data), options)
}

export default useAppointmentCancel
