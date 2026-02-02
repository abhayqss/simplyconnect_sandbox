import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './contactListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findContacts(params)
})