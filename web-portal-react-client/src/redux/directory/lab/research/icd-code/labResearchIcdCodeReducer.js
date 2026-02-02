import InitialState from './LabResearchIcdCodeInitialState'

import listReducer from './list/labResearchIcdCodeReasonListReducer'

const initialState = new InitialState()

export default function labResearchIcdCodeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
