import { Actions } from 'redux/utils/Value'

import actionTypes from './canDownloadInTuneReportActionTypes'

import service from 'services/AssessmentService'

export default Actions({
    actionTypes,
    doLoad: params => service.canDownloadInTuneReport(params)
})