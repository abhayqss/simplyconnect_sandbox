import InitialState from './CareTeamMemberInitialState'

import careTeamMemberCountReducer from './count/careTeamMemberCountReducer'
import canCareTeamMemberReducer from './can/canCareTeamMemberReducer'

const initialState = new InitialState()

export default function careTeamMemberReducer(state = initialState, action) {
    let nextState = state

    const count = careTeamMemberCountReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const can = canCareTeamMemberReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    return nextState
}