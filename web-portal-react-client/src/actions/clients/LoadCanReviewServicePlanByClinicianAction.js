import Factory from '../ActionFactory'

import actions from 'redux/client/servicePlan/can/review-by-clinician/canReviewServicePlanByClinicianActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})