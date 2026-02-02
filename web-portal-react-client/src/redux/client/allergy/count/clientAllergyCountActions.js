import { Actions } from 'redux/utils/Value'

import service from 'services/ClientAllergyService'

import actionTypes from './clientAllergyCountActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params) => service.count(params)
})