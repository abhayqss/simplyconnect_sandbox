import Factory from '../ActionFactory'

import actions from 'redux/lab/research/order/can/add/canAddLabResearchOrderActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})