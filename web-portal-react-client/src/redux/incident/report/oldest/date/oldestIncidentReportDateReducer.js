import { Reducer } from 'redux/utils/Value'

import actionTypes from './oldestIncidentReportDateActionTypes'
import InitialState from './OldestIncidentReportDateInitialState'

export default Reducer({
    actionTypes,
    stateClass: InitialState
})