import { Actions } from 'redux/utils/Details'

import actionTypes from './actionTypes'

import service from 'services/DocumentESignService'

export default Actions({
    actionTypes,
    doLoad: (params) => service.canAddSignature(params)
})