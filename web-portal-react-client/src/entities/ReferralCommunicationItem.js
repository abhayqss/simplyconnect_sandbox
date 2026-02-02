const { Record } = require('immutable')

const ReferralCommunicationItem = Record({
    text: '',
    date: null,
    authorPhone: '',
    authorFullName: '',
})

export default ReferralCommunicationItem
