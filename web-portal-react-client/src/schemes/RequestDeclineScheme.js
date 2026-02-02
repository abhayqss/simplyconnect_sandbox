import { Shape, integer } from './types'

const RequestDeclineScheme = Shape({
    referralDeclineReasonId: integer().required()
})

export default RequestDeclineScheme
