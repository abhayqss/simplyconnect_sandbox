import { Actions } from 'redux/utils/List'

import service from 'services/ClientAllergyService'

import actionTypes from './clientAllergyListActionTypes'

export default Actions({
    actionTypes,
    doLoad: params => service.find(params)
})