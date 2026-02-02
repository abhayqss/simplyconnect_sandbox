import { Actions } from 'redux/utils/Form'

import actionTypes from './labResearchOrderFormActionTypes'

import service from 'services/LabResearchOrderService'

export default Actions({
    actionTypes,
    doSubmit: data => service.save(data)
})