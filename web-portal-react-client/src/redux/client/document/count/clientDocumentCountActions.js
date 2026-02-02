import { Actions } from 'redux/utils/Value'

import service from 'services/ClientDocumentService'

import actionTypes from './clientDocumentCountActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params) => service.count(params)
})