import { Actions } from 'redux/utils/Details'

import actionTypes from './actionTypes'

import service from 'services/ClientService'

export default Actions({
    actionTypes,
    doLoad: (params) => service.canAdd(params)
})