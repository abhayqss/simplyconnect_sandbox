import Factory from '../ActionFactory'

import * as actions from 'redux/client/assessment/can/add/canAddAssessmentActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId })
})