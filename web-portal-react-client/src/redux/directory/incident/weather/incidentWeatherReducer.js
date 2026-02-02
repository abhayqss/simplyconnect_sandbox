import conditionReducer from './condition/incidentWeatherConditionReducer'

import InitialState from './IncidentWeatherInitialState'

const initialState = InitialState()

export default function incidentWeatherReducer(state = initialState, action) {
    let nextState = state

    const condition = conditionReducer(state.condition, action)
    if (condition !== state.condition) nextState = nextState.setIn(['condition'], condition)

    return nextState
}