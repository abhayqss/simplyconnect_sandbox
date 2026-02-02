import typeReducer from './type/incidentWeatherConditionTypeReducer'

import InitialState from './IncidentWeatherConditionInitialState'

const initialState = InitialState()

export default function incidentWeatherConditionReducer(state = initialState, action) {
    let nextState = state

    const type = typeReducer(state.type, action)
    if (type !== state.type) nextState = nextState.setIn(['type'], type)

    return nextState
}