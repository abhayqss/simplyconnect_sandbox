import InitialState from './EventPageInitialState'

import numberReducer from './number/eventPageNumberReducer'

const initialState = new InitialState()

export default function eventPageReducer(state = initialState, action) {
    let nextState = state

    const number = numberReducer(state.number, action)
    if (number !== state.number) nextState = nextState.setIn(['number'], number)

    return nextState
}