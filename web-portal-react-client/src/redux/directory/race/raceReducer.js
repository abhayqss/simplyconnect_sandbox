import InitialState from './RaceInitialState'

import listReducer from './list/raceListReducer'

const initialState = new InitialState()

export default function raceReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
