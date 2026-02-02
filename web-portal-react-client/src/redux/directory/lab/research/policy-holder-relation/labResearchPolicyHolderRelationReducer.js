import InitialState from './LabResearchPolicyHolderRelationInitialState'

import listReducer from './list/labResearchPolicyHolderRelationListReducer'

const initialState = new InitialState()

export default function labResearchPolicyHolderRelationReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
