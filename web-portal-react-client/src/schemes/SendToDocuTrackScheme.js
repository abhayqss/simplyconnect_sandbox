import { Shape, string } from './types'

const SendToDocuTrackScheme = Shape({
    businessUnitCode: string().nullable()
})

export default SendToDocuTrackScheme
