import { Actions } from 'redux/utils/Form'

import actionTypes from './clientEssentialsFormActionTypes'

import service from 'services/ClientService'

export default Actions({
    actionTypes,
    doSubmit: data => service.saveEssentials(data)
})
