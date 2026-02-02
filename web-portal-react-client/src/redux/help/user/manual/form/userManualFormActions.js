import { Actions } from 'redux/utils/Form'

import actionTypes from './userManualFormActionTypes'

import service from 'services/UserManualService'

export default Actions({
    actionTypes,
    doSubmit: data => service.save(data)
})