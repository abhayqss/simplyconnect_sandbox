import { Actions } from 'redux/utils/List'

import actionTypes from './actionTypes'

import service from 'services/CategoryService'

export default Actions({
    actionTypes,
    doLoad: (params) => service.find(params)
})