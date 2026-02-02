import Factory from '../ActionFactory'

import actions from 'redux/client/unassociated/list/unassociatedClientListActions'

export default Factory(actions, {
    action: (params, actions) => actions.clear()
})