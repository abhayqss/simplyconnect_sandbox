import { Actions } from 'redux/utils/List'

import actionTypes from './userManualListActionTypes'

import service from 'services/UserManualService'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.find(params)
})