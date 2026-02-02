import { Actions } from 'redux/utils/Value'

import actionTypes from './latestIncidentReportDateActionTypes'

import service from 'services/IncidentReportService'

export default Actions({
    actionTypes,
    doLoad: params => service.findLatestDate(params)
})