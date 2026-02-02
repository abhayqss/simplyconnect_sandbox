import { useMutation } from '@tanstack/react-query'

import service from 'services/AppointmentService'

function download(params) {
    return service.export(params)
}

function useAppointmentExport(options) {
    return useMutation(download, options)
}

export default useAppointmentExport
