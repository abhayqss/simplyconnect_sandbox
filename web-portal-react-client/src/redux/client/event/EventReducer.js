import InitialState from './EventInitialState'

import notViewableReducer from './not-viewable/notViewableEventReducer'

const initialState = new InitialState()

export default function eventReducer(state = initialState, action) {
    let nextState = state

    const notViewable = notViewableReducer(state.notViewable, action)
    if (notViewable !== state.notViewable) nextState = nextState.setIn(['notViewable'], notViewable)

    return nextState
}