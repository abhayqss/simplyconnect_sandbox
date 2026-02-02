import { Reducer } from 'redux/utils/Delete'

import actionTypes from './incidentReportDeletionActionTypes'
import InitialState from './IncidentReportDeletionInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})