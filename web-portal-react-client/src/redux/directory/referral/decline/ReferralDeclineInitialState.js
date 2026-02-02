import Reason from './reason/ReferralDeclineReasonInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    reason: Reason(),
})

export default InitialState