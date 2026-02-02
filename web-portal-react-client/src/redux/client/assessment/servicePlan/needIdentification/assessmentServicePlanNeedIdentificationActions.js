import { Actions } from 'redux/utils/Form'
import service from 'services/AssessmentService'

import actionTypes from './assessmentServicePlanNeedIdentificationActionTypes'

export default Actions({
    actionTypes,
    doSubmit: (data, params) => service.saveServicePlanNeedIdentification(data, params)
})