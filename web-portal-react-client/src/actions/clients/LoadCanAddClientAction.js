import Factory from '../ActionFactory'

import actions from 'redux/client/can/add/canAddClientActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})