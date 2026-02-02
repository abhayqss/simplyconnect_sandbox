import { Actions } from 'redux/utils/Details'

import service from 'services/SDoHReportService'

import actionTypes from './sendSDoHReportActionTypes'

export default Actions({
    actionTypes,
    doLoad: (reportId, params) => service.markAsSent(reportId, params)
})