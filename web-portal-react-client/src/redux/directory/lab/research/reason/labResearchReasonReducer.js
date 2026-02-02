import InitialState from './LabResearchReasonInitialState'

import listReducer from './list/labResearchReasonListReducer'

const initialState = new InitialState()

export default function labResearchReasonReducer (state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
