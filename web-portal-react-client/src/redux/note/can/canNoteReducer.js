import OrganizationInitialState from './CanNoteInitialState'

import addReducer from './add/canAddNoteReducer'
import viewReducer from './view/canViewNotesReducer'

const initialState = new OrganizationInitialState()

export default function canNoteReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}