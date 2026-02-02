import InitialState from './LabResearchOrderStatusInitialState'

import listReducer from './list/labResearchOrderStatusListReducer'

const initialState = new InitialState()

export default function labResearchOrderStatusReducer (state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
