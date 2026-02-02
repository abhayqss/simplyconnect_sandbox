import Factory from '../ActionFactory'

import * as actions from 'redux/directory/emergency/service/list/emergencyServiceListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})