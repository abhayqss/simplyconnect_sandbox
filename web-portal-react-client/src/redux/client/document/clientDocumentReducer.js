import InitialState from './ClientDocumentInitialState'

import canReducer from './can/canClientDocumentReducer'
import formReducer from './form/clientDocumentFormReducer'
import listReducer from './list/clientDocumentListReducer'
import countReducer from './count/clientDocumentCountReducer'
import detailsReducer from './details/clientDocumentDetailsReducer'
import deletionReducer from './deletion/сlientDocumentDeletionReducer'

import historyReducer from './history/clientDocumentHistoryReducer'

const initialState = new InitialState()

export default function clientDocumentReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const form = formReducer(state.form, action)
    if (form !== state.form) nextState = nextState.setIn(['form'], form)

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const history = historyReducer(state.history, action)
    if (history !== state.history) nextState = nextState.setIn(['history'], history)

    const deletion = deletionReducer(state.deletion, action)
    if (deletion !== state.deletion) nextState = nextState.setIn(['deletion'], deletion)

    return nextState
}