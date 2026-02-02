import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './actionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findClients(params)
})