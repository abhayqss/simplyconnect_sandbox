import listReducer from './list/incidentWeatherConditionTypeListReducer'

import InitialState from './IncidentWeatherConditionTypeInitialState'

const initialState = InitialState()

export default function incidentWeatherConditionTypeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}