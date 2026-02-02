import ReferralCommunicationItem from './ReferralCommunicationItem'

const { Record } = require('immutable')

const ReferralInfoResponse = Record({
    id: null,
    subject: '',
    statusName: '',
    statusTitle: '',
    request: ReferralCommunicationItem(),
    response: ReferralCommunicationItem(),
})

export default ReferralInfoResponse
