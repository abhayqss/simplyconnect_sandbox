import { Actions } from 'redux/utils/Value'

import actionTypes from './canGenerateInTuneReportActionTypes'

import service from 'services/AssessmentService'

export default Actions({
    actionTypes,
    doLoad: params => service.canGenerateInTuneReport(params)
})