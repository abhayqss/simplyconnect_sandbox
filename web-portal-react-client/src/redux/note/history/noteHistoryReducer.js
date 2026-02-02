import InitialState from './NoteHistoryInitialState'

import listReducer from './list/noteHistoryListReducer'
import detailsReducer from './details/noteHistoryDetailsReducer'

const initialState = new InitialState()

export default function noteHistoryReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}