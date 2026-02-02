import { Actions } from 'redux/utils/Value'

import actionTypes from './canViewCategoriesActionTypes'

import service from 'services/CategoryService'

export default Actions({
    actionTypes,
    doLoad: params => service.canView(params)
})