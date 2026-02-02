import { Actions } from 'redux/utils/Details'

import service from 'services/ClientProblemService'

import actionTypes from './clientProblemDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: ({ clientId, ...params }) => service.findById(clientId, params)
})