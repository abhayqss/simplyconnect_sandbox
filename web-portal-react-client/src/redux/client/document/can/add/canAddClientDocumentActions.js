import { Actions } from 'redux/utils/Details'

import actionTypes from './canAddClientDocumentActionTypes'

import service from 'services/ClientDocumentService'

export default Actions({
    actionTypes,
    doLoad: params => service.canAdd(params)
})