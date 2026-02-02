import InitialState from './ClientRecordInitialState'

import listReducer from './list/clientRecordListReducer'

const initialState = new InitialState()

export default function reducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}