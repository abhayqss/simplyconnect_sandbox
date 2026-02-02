import InitialState from './NotePageInitialState'

import numberReducer from './number/notePageNumberReducer'

const initialState = new InitialState()

export default function notePageReducer(state = initialState, action) {
    let nextState = state

    const number = numberReducer(state.number, action)
    if (number !== state.number) nextState = nextState.setIn(['number'], number)

    return nextState
}