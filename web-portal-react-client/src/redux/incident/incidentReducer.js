import reportReducer from './report/incidentReportReducer'
import pictureReducer from './picture/incidentPictureReducer'

import InitialState from './IncidentInitialState'

const initialState = InitialState()

export default function labOrderReducer(state = initialState, action) {
    let nextState = state

    const report = reportReducer(state.report, action)
    if (report !== state.report) nextState = nextState.setIn(['report'], report)
    
    const picture = pictureReducer(state.picture, action)
    if (picture !== state.picture) nextState = nextState.setIn(['picture'], picture)

    return nextState
}