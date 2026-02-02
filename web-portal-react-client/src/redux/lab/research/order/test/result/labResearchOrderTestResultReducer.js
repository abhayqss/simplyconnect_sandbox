import InitialState from './LabResearchOrderTestResultInitialState'

import listReducer from './list/labResearchOrderTestResultListReducer'

const initialState = new InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}