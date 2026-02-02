import InitialState from './OldestIncidentReportInitialState'

import dateReducer from './date/oldestIncidentReportDateReducer'

const initialState = new InitialState()

export default function oldestIncidentReportReducer(state = initialState, action) {
    let nextState = state

    const date = dateReducer(state.date, action)
    if (date !== state.date) nextState = nextState.setIn(['date'], date)

    return nextState
}