import Factory from '../ActionFactory'

import * as actions from 'redux/client/servicePlan/can/view/canViewServicePlanActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId })
})