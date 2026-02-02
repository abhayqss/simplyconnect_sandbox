import { Reducer } from 'redux/utils/List'

import actionTypes from './incidentReportClassMemberTypeListActionTypes'
import InitialState from './IncidentReportClassMemberListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})