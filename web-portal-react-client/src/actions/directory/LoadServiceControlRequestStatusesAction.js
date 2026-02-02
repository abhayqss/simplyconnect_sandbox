import Factory from '../ActionFactory'

import actions from 'redux/directory/service/control/request/status/list/serviceControlRequestStatusListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})