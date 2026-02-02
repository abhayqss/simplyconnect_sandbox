import InitialState from './ReferralInitialState'

import listReducer from './list/referralListReducer'
import detailsReducer from './details/referralDetailsReducer'

import infoReducer from './info/referralInfoReducer'
import communityReducer from './community/communityReducer'
import requestReducer from './request/referralRequestReducer'
import recipientReducer from './recipient/referralRecipientReducer'

const initialState = new InitialState()

export default function referralReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const info = infoReducer(state.info, action)
    if (info !== state.info) nextState = nextState.setIn(['info'], info)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const request = requestReducer(state.request, action)
    if (request !== state.request) nextState = nextState.setIn(['request'], request)

    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)
    
    const recipient = recipientReducer(state.recipient, action)
    if (recipient !== state.recipient) nextState = nextState.setIn(['recipient'], recipient)

    return nextState
}