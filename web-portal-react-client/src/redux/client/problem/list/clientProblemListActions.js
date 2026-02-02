import { Actions } from 'redux/utils/List'

import service from 'services/ClientProblemService'

import actionTypes from './clientProblemListActionTypes'

export default Actions({
    actionTypes,
    doLoad: ({ clientId, ...params }) => service.find(clientId, params)
})