import { Actions } from 'redux/utils/List'

import service from 'services/ClientDashboardService'

import actionTypes from './clientNoteListActionTypes'

export default Actions({
    actionTypes,
    isSortable: false,
    isFilterable: false,
    doLoad: params => service.findNotes(params)
})