import InitialState from './CanCareTeamMemberInitialState'
import canAddCareTeamMemberReducer from './add/canAddCareTeamMemberReducer'
import canViewCareTeamMemberReducer from './view/canViewCareTeamMemberReducer'

const initialState = new InitialState()

export default function careTeamMemberReducer(state = initialState, action) {
    let nextState = state

    const add = canAddCareTeamMemberReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    const view = canViewCareTeamMemberReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}