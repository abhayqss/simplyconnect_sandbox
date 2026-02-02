import InitialState from './InTuneReportInitialState'

import canReducer from './can/canInTuneReportReducer'
import detailsReducer from './details/inTuneReportDetailsReducer'

const initialState = InitialState()

export default function canAssessmentReducer(state = initialState, action) {
    let nextState = state

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}
