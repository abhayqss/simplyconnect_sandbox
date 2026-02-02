import Factory from '../ActionFactory'

import actions from 'redux/directory/service/status/list/serviceStatusListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})