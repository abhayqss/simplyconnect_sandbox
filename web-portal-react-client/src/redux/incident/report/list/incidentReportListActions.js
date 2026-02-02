import { Actions } from 'redux/utils/List'

import actionTypes from './incidentReportListActionTypes'

import service from 'services/IncidentReportService'

export default Actions({
    actionTypes,
    doLoad: (params, options) => service.find(params, options)
})