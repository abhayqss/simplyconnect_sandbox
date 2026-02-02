import { Actions } from 'redux/utils/List'

import service from 'services/ClientDashboardService'

import actionTypes from './clientEventListActionTypes'

export default Actions({
    actionTypes,
    isSortable: false,
    isFilterable: false,
    doLoad: params => service.findEvents(params)
})