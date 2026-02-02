import InitialState from './ReportInitialState'

import canReducer from './can/canReportReducer'
import listReducer from './list/reportListReducer'
import sdohReducer from './sdoh/sDoHReportReducer'
import documentReducer from './document/reportDocumentReducer'

const initialState = new InitialState()

export default function reportReducer (state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)
    
    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const sdoh = sdohReducer(state.sdoh, action)
    if (sdoh !== state.sdoh) nextState = nextState.setIn(['sdoh'], sdoh)

    const document = documentReducer(state.document, action)
    if (document !== state.document) nextState = nextState.setIn(['document'], document)

    return nextState
}