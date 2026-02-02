import { Shape, string } from './types'

import ReferralCommunicationItem from './ReferralCommunicationItem'

const RequestInfoScheme = Shape({
    subject: string().required(),
    request: ReferralCommunicationItem,
})

export default RequestInfoScheme
