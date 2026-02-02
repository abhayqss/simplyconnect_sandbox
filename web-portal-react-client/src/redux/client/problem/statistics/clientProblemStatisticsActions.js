import { Actions } from 'redux/utils/List'

import service from 'services/ClientProblemService'

import actionTypes from './clientProblemStatisticsActionTypes'

export default Actions({
    actionTypes,
    doLoad: ({ clientId }) => service.findStatisticsById(clientId)
})