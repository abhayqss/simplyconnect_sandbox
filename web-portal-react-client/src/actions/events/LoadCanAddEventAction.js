import Factory from '../ActionFactory'

import actions from 'redux/event/can/add/canAddEventActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId })
})