import { Actions } from 'redux/utils/Value'

import actionTypes from './canAddCategoriesActionTypes'

import service from 'services/CategoryService'

export default Actions({
    actionTypes,
    doLoad: params => service.canAdd(params)
})