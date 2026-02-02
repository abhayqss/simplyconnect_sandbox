import { Actions } from 'redux/utils/Details'

import service from 'services/ClientAllergyService'

import actionTypes from './clientAllergyDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: (...args) => service.findById(...args)
})