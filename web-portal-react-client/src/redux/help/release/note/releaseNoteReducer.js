import canReducer from './can/canReleaseNoteReducer'
import listReducer from './list/releaseNoteListReducer'
import detailsReducer from './details/releaseNoteDetailsReducer'
import deletionReducer from './deletion/releaseNoteDeletionReducer'

import InitialState from './ReleaseNoteInitialState'

const initialState = InitialState()

export default function releaseNoteReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const deletion = deletionReducer(state.deletion, action)
    if (deletion !== state.deletion) nextState = nextState.setIn(['deletion'], deletion)

    return nextState
}