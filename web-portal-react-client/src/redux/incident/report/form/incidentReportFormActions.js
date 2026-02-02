import { Actions } from 'redux/utils/Form'

import actionTypes from './incidentReportFormActionTypes'

import service from 'services/IncidentReportService'

export default Actions({
    actionTypes,
    doSubmit: (data, isDraft) => service.save(data, isDraft)
})