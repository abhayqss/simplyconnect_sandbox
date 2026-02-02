import InitialState from './CareTeamMemberContactOrganizationInitialState'

import listReducer from './list/careTeamMemberContactOrganizationListReducer'

const initialState =  InitialState()

export default function organizationReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}