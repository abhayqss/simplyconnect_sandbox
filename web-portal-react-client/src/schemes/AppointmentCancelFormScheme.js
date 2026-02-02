import { Shape, string } from './types'

const AppointmentCancelFormScheme = Shape({
    cancelReason: string().nullable().required(),
})

export default AppointmentCancelFormScheme