import Factory from '../ActionFactory'

import actions from 'redux/marketplace/can/view/canViewMarketplaceActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})
