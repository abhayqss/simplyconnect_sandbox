import InitialState from './CanCareTeamMemberAffiliatedInitialState'
import canAddCareTeamMemberReducer from './add/canAddCareTeamMemberAffiliatedReducer'

const initialState = new InitialState()

export default function careTeamMemberReducer(state = initialState, action) {
    let nextState = state

    const add = canAddCareTeamMemberReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    return nextState
}