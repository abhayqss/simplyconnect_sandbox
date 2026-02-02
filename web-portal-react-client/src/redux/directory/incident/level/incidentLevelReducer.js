import reportingSettingReducer from './reporting-setting/incidentLevelReportingSettingReducer'

import InitialState from './IncidentLevelInitialState'

const initialState = InitialState()

export default function incidentLevelReducer(state = initialState, action) {
    let nextState = state

    const reportingSetting = reportingSettingReducer(state.reportingSetting, action)
    if (reportingSetting !== state.reportingSetting) nextState = nextState.setIn(['reportingSetting'], reportingSetting)

    return nextState
}