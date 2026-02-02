import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './labResearchReasonListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: () => service.findLabResearchReasons()
})