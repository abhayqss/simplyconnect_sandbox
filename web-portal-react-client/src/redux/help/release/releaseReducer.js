import noteReducer from './note/releaseNoteReducer'
import notificationReducer from './notification/releaseNotificationReducer'

import InitialState from './ReleaseInitialState'

const initialState = InitialState()

export default function releaseReducer(state = initialState, action) {
    let nextState = state

    const note = noteReducer(state.note, action)
    if (note !== state.note) nextState = nextState.setIn(['note'], note)
    
    const notification = notificationReducer(state.notification, action)
    if (notification !== state.notification) nextState = nextState.setIn(['notification'], notification)

    return nextState
}