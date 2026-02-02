import { Actions } from 'redux/utils/Value'

import actionTypes from './canMarkAsSentSDoHReportActionTypes'

import service from 'services/SDoHReportService'

export default Actions({
    actionTypes,
    doLoad: ({ reportId, ...params }) => service.canMarkAsSent(reportId, params)
})