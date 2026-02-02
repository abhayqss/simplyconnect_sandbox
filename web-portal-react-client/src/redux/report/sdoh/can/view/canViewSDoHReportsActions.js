import { Actions } from 'redux/utils/Value'

import actionTypes from './canViewSDoHReportsActionTypes'

import service from 'services/SDoHReportService'

export default Actions({
    actionTypes,
    doLoad: () => service.canView()
})