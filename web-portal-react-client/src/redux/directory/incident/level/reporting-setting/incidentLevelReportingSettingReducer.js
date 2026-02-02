import listReducer from './list/incidentLevelReportingSettingListReducer'

import InitialState from './IncidentLevelReportingSettingInitialState'

const initialState = InitialState()

export default function reportingSettingReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}