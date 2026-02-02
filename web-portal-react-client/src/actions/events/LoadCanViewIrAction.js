import Factory from '../ActionFactory'

import actions from 'redux/event/ir/can/view/canViewIrActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})