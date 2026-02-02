import InitialState from './ResourceNameInitialState'
import listReducer from './list/resourceNameListReducer'

const initialState = new InitialState()

export default function noteReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}