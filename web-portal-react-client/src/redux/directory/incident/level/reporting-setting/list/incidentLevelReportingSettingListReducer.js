import { Reducer } from 'redux/utils/List'

import actionTypes from './incidentLevelReportingSettingListActionTypes'
import InitialState from './IncidentLevelReportingSettingListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})