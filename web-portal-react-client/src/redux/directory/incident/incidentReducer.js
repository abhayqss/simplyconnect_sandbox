import typeReducer from './type/incidentTypeReducer'
import placeReducer from './place/incidentPlaceReducer'
import levelReducer from './level/incidentLevelReducer'
import reportReducer from './report/incidentReportReducer'
import weatherReducer from './weather/incidentWeatherReducer'

import InitialState from './IncidentInitialState'

const initialState = InitialState()

export default function incidentReducer(state = initialState, action) {
    let nextState = state

    const report = reportReducer(state.report, action)
    if (report !== state.report) nextState = nextState.setIn(['report'], report)

    const place = placeReducer(state.place, action)
    if (place !== state.place) nextState = nextState.setIn(['place'], place)

    const level = levelReducer(state.level, action)
    if (level !== state.level) nextState = nextState.setIn(['level'], level)

    const type = typeReducer(state.type, action)
    if (type !== state.type) nextState = nextState.setIn(['type'], type)

    const weather = weatherReducer(state.weather, action)
    if (weather !== state.weather) nextState = nextState.setIn(['weather'], weather)

    return nextState
}