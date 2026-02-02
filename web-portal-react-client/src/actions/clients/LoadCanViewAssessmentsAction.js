import Factory from '../ActionFactory'

import actions from 'redux/client/assessment/can/view/canViewAssessmentsActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId })
})