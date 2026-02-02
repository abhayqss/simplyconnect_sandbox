import { Actions } from 'redux/utils/Value'

import actionTypes from './canViewIncidentReportsActionTypes'

import service from 'services/IncidentReportService'

export default Actions({
    actionTypes,
    doLoad: params => service.canView(params)
})