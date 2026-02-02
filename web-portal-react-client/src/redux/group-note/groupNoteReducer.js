import InitialState from './GroupNoteInitialState'

import formReducer from './form/groupNoteFormReducer'
import detailsReducer from './details/groupNoteDetailsReducer'

const initialState = new InitialState()

export default function noteReducer(state = initialState, action) {
    let nextState = state

    const form = formReducer(state.form, action)
    if (form !== state.form) nextState = nextState.setIn(['form'], form)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}