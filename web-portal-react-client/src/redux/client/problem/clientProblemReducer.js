import InitialState from './ClientProblemInitialState'

import listReducer from './list/clientProblemListReducer'
import detailsReducer from './details/clientProblemDetailsReducer'
import statisticsReducer from './statistics/clientProblemStatisticsReducer'

const initialState = InitialState()

export default function clientProblemReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const statistics = statisticsReducer(state.statistics, action)
    if (statistics !== state.statistics) nextState = nextState.setIn(['statistics'], statistics)

    return nextState
}