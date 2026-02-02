import Factory from '../ActionFactory'

import * as actions from 'redux/directory/insurance/payment/plan/list/insurancePaymentPlanListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})