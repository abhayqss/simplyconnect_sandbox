import InitialState from './EventInitialState'

import irReducer from './ir/irReducer'
import canReducer from './can/canEventReducer'
import listReducer from './list/eventListReducer'
import formReducer from './form/eventFormReducer'
import pageReducer from './page/eventPageReducer'
import noteReducer from './note/eventNoteReducer'
import detailsReducer from './details/eventDetailsReducer'
import communityReducer from './community/communityReducer'
import notificationReducer from './notification/eventNotificationReducer'

const initialState = new InitialState()

export default function eventReducer(state = initialState, action) {
    let nextState = state

    const ir = irReducer(state.ir, action)
    if (ir !== state.ir) nextState = nextState.setIn(['ir'], ir)

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const form = formReducer(state.form, action)
    if (form !== state.form) nextState = nextState.setIn(['form'], form)

    const page = pageReducer(state.page, action)
    if (page !== state.page) nextState = nextState.setIn(['page'], page)

    const note = noteReducer(state.note, action)
    if (note !== state.note) nextState = nextState.setIn(['note'], note)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)

    const notification = notificationReducer(state.notification, action)
    if (notification !== state.notification) nextState = nextState.setIn(['notification'], notification)

    return nextState
}