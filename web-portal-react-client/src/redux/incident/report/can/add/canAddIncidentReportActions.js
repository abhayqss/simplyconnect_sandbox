import { Actions } from 'redux/utils/Value'

import actionTypes from './canAddIncidentReportActionTypes'

import service from 'services/IncidentReportService'

export default Actions({
    actionTypes,
    doLoad: params => service.canAdd(params)
})