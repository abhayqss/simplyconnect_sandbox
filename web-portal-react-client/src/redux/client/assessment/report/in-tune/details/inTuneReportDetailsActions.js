import { Actions } from 'redux/utils/Details'

import service from 'services/AssessmentService'

import actionTypes from './inTuneReportDetailsActionTypes'

export default Actions({
    actionTypes,
    doDownload: (params) => service.downloadInTuneReport(params)
})