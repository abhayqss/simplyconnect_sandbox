import InitialState from './LabResearchOrderTestInitialState'

import resultReducer from './result/labResearchOrderTestResultReducer'

const initialState = new InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const result = resultReducer(state.result, action)
    if (result !== state.result) nextState = nextState.setIn(['result'], result)

    return nextState
}