import { useMemo } from 'react'

import useDetails from 'hooks/common/useDetails'

import service from 'services/AppointmentService'

const options = {
    doLoad: ({ appointmentId }) => service.findById(appointmentId),
}

function useAppointmentDetails(appointmentId) {
    const params = useMemo(() => ({ appointmentId }), [appointmentId])

    return useDetails('APPOINTMENT', params, options)
}

export default useAppointmentDetails
