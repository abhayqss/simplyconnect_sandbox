import InitialState from './IncidentReportConversationInitialState'

import joinReducer from './join/incidentReportConversationJoinReducer'

const initialState = new InitialState()

export default function canReferralRequestReducer(state = initialState, action) {
    let nextState = state

    const join = joinReducer(state.join, action)
    if (join !== state.join) nextState = nextState.setIn(['join'], join)
    
    return nextState
}