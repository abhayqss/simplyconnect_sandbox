import { Actions } from 'redux/utils/List'

import actionTypes from './communityListActionTypes'

import service from 'services/DirectoryService'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findCommunities(params)
})