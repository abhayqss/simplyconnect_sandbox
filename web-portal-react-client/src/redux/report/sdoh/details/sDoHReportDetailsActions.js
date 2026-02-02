import { Actions } from 'redux/utils/Details'

import service from 'services/SDoHReportService'

import actionTypes from './sDoHReportActionTypes'

export default Actions({
    actionTypes,
    doDownload: (reportId, params) => service.downloadById(reportId, params)
})