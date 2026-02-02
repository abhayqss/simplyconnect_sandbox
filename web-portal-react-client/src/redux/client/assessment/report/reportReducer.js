import InitialState from './ReportInitialState'

import inTuneReducer from './in-tune/inTuneReportReducer'

const initialState = InitialState()

export default function canAssessmentReducer(state = initialState, action) {
    let nextState = state

    const inTune = inTuneReducer(state.inTune, action)
    if (inTune !== state.inTune) nextState = nextState.setIn(['inTune'], inTune)

    return nextState
}
