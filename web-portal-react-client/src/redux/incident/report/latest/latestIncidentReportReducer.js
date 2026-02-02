import InitialState from './LatestIncidentReportInitialState'

import dateReducer from './date/latestIncidentReportDateReducer'

const initialState = new InitialState()

export default function latestIncidentReportReducer(state = initialState, action) {
    let nextState = state

    const date = dateReducer(state.date, action)
    if (date !== state.date) nextState = nextState.setIn(['date'], date)

    return nextState
}