import { Actions } from 'redux/utils/Value'

import actionTypes from './oldestIncidentReportDateActionTypes'

import service from 'services/IncidentReportService'

export default Actions({
    actionTypes,
    doLoad: params => service.findOldestDate(params)
})