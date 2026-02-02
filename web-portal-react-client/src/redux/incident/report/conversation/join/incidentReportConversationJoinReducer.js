import { Reducer } from 'redux/utils/Send'

import actionTypes from './incidentReportConversationJoinActionTypes'
import InitialState from './IncidentReportConversationJoinInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})