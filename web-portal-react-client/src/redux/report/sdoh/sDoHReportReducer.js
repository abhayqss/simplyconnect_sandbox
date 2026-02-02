import InitialState from './SDoHReportInitialState'

import canReducer from './can/canSDoHReportReducer'
import listReducer from './list/sDoHReportListReducer'
import sendReducer from './send/sendSDoHReportReducer'
import communityReducer from './community/communityReducer'
import detailsReducer from './details/sDoHReportDetailsReducer'

const initialState = new InitialState()

export default function SDoHReportReducer (state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)
    
    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const send = sendReducer(state.send, action)
    if (send !== state.send) nextState = nextState.setIn(['send'], send)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)

    return nextState
}