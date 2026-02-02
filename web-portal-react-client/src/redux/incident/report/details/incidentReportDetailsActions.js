import { Actions } from 'redux/utils/Details'

import service from 'services/IncidentReportService'

import actionTypes from './incidentReportDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: (reportId, params) => service.findById(reportId, params),
    doDownload: (reportId, params) => service.downloadById(reportId, params)
})