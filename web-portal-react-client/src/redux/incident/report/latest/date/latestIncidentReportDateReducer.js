import { Reducer } from 'redux/utils/Value'

import actionTypes from './latestIncidentReportDateActionTypes'
import InitialState from './LatestIncidentReportDateInitialState'

export default Reducer({
    actionTypes,
    stateClass: InitialState
})