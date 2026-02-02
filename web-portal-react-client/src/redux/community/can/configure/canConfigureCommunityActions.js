import { Actions } from 'redux/utils/Value'

import actionTypes from './canConfigureCommunityActionTypes'

import service from 'services/CommunityService'

export default Actions({
    actionTypes,
    doLoad: params => service.canConfigure(params)
})