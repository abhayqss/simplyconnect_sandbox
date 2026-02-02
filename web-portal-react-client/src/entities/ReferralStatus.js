const { Record } = require('immutable')

const RequestStatus = Record({
    status: '',
    comment: '',
    referralDeclineReasonId: null
})

export default RequestStatus
