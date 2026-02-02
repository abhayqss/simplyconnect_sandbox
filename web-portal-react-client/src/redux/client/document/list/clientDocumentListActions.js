import { Actions } from 'redux/utils/List'

import service from 'services/ClientDocumentService'

import actionTypes from './clientDocumentListActionTypes'

export default Actions({
    actionTypes,
    doLoad: params => service.find(params)
})