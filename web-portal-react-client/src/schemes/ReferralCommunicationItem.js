import { Shape, string, phoneNumber } from './types'

const ReferralCommunicationItem = Shape({
    text: string().required(),
    authorPhone: phoneNumber().required(),
    authorFullName: string().required(),
})

export default ReferralCommunicationItem
