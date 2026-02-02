import { Actions } from 'redux/utils/Details'

import actionTypes from './actionTypes'

import service from 'services/PrivateMarketplaceCommunityService'

export default Actions({
    actionTypes,
    doLoad: () => service.canView()
})