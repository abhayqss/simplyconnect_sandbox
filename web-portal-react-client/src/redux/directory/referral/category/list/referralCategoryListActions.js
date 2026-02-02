import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './referralCategoryListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findReferralCategories(params)
})