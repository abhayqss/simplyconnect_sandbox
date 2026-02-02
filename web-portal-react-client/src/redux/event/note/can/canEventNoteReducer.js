import OrganizationInitialState from './CanEventNoteInitialState'

import addReducer from './add/canAddEventNoteReducer'

const initialState = new OrganizationInitialState()

export default function canEventNoteReducer(state = initialState, action) {
    let nextState = state

    const add = addReducer(state.add, action)
    if (add !== state.add) nextState = nextState.setIn(['add'], add)

    return nextState
}