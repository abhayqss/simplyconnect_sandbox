import Can from './can/CanReferralRequestInitialState'
import List from './list/ReferralRequestListInitialState'
import Details from './details/ReferralRequestDetailsInitialState'
import Default from './default/ReferralRequestDefaultInitialState'

import Sender from './sender/ReferralRequestSenderInitialState'
import Contact from './contact/ReferralRequestContactInitialState'

import Assign from './assign/ReferralRequestAssignInitialState'
import Accept from './accept/ReferralRequestAcceptInitialState'
import Cancel from './cancel/ReferralRequestCancelInitialState'
import Decline from './decline/ReferralRequestDeclineInitialState'
import Unassign from './unassign/ReferralRequestUnassignInitialState'
import Preadmit from './pre-admit/ReferralRequestPreadmitInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    sender: Sender(),
    contact: Contact(),
    assign: Assign(),
    accept: Accept(),
    cancel: Cancel(),
    decline: Decline(),
    details: Details(),
    default: Default(),
    unassign: Unassign(),
    preadmit: Preadmit(),
})