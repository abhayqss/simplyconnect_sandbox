import { useMemo } from 'react'

import useList from 'hooks/common/useList'

import service from 'services/AppointmentService'

const options = {
    isMinimal: true,
    doLoad: params => service.findAppointmentHistory(params),
}

function useAppointmentHistoryList(appointmentId) {
    let params = useMemo(() => ({ appointmentId }), [appointmentId])

    return useList('APPOINTMENT_HISTORY', params, options)
}

export default useAppointmentHistoryList
