import { Actions } from 'redux/utils/List'

import service from 'services/LabResearchOrderService'

import actionTypes from './labResearchSpecimenTypeListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findSpecimensTypes(params)
})