import InitialState from './ReferralRequestInitialState'

import canReducer from './can/canReferralRequestReducer'
import listReducer from './list/referralRequestListReducer'
import detailsReducer from './details/referralRequestDetailsReducer'
import defaultReducer from './default/referralRequestDefaultReducer'

import senderReducer from './sender/referralRequestSenderReducer'
import contactReducer from './contact/referralRequestContactReducer'

import assignReducer from './assign/referralRequestAssignReducer'
import acceptReducer from './accept/referralRequestAcceptReducer'
import cancelReducer from './cancel/referralRequestCancelReducer'
import declineReducer from './decline/referralRequestDeclineReducer'
import unassignReducer from './unassign/referralRequestUnassignReducer'
import preadmitReducer from './pre-admit/referralRequestPreadmitReducer'

const initialState = new InitialState()

export default function referralRequestReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)
    
    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const sender = senderReducer(state.sender, action)
    if (sender !== state.sender) nextState = nextState.setIn(['sender'], sender)

    const contact = contactReducer(state.contact, action)
    if (contact !== state.contact) nextState = nextState.setIn(['contact'], contact)

    const assign = assignReducer(state.assign, action)
    if (assign !== state.assign) nextState = nextState.setIn(['assign'], assign)
    
    const unassign = unassignReducer(state.unassign, action)
    if (unassign !== state.unassign) nextState = nextState.setIn(['unassign'], unassign)
    
    const accept = acceptReducer(state.accept, action)
    if (accept !== state.accept) nextState = nextState.setIn(['accept'], accept)

    const cancel = cancelReducer(state.cancel, action)
    if (cancel !== state.cancel) nextState = nextState.setIn(['cancel'], cancel)

    const decline = declineReducer(state.decline, action)
    if (decline !== state.decline) nextState = nextState.setIn(['decline'], decline)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const defaultReferral = defaultReducer(state.default, action)
    if (defaultReferral !== state.default) nextState = nextState.setIn(['default'], defaultReferral)

    const preadmit = preadmitReducer(state.preadmit, action)
    if (preadmit !== state.preadmit) nextState = nextState.setIn(['preadmit'], preadmit)

    return nextState
}