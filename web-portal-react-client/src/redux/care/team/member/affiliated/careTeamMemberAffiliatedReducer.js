import InitialState from './CareTeamMemberAffiliatedInitialState'
import canCareTeamMemberReducer from './can/canCareTeamMemberAffiliatedReducer'

const initialState = new InitialState()

export default function careTeamMemberAffiliatedReducer(state = initialState, action) {
    let nextState = state

    const can = canCareTeamMemberReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    return nextState
}