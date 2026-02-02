import Factory from '../ActionFactory'

import actions from 'redux/community/can/configure/canConfigureCommunityActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})