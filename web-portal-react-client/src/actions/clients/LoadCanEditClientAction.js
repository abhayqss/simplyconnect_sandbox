import Factory from '../ActionFactory'

import * as actions from 'redux/client/can/edit/canEditClientActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load(clientId)
})