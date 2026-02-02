import { Actions } from 'redux/utils/Send'

import service from 'services/IncidentReportService'

import actionTypes from './incidentReportConversationJoinActionTypes'

export default Actions({
    actionTypes,
    doSend: reportId => service.joinToConversationById(reportId)
})