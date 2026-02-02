import { Actions } from 'redux/utils/List'

import actionTypes from './sDoHReportListActionTypes'

import service from 'services/SDoHReportService'

export default Actions({
    actionTypes,
    doLoad: params => service.find(params)
})