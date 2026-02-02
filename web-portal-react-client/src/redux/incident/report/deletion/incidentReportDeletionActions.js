import { Actions } from 'redux/utils/Delete'

import service from 'services/IncidentReportService'

import actionTypes from './incidentReportDeletionActionTypes'

export default Actions({
    actionTypes,
    doDelete: (reportId) => service.deleteById(reportId)
})