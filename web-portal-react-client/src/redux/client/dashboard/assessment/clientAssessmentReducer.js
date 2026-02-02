import InitialState from './ClientAssessmentInitialState'

import listReducer from './list/clientAssessmentListReducer'
import statisticsReducer from './statistics/clientAssessmentStatisticsReducer'

const initialState = new InitialState()

export default function clientAssessmentReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)
    
    const statistics = statisticsReducer(state.statistics, action)
    if (statistics !== state.statistics) nextState = nextState.setIn(['statistics'], statistics)

    return nextState
}