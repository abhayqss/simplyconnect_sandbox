import Factory from '../ActionFactory'

import actions from 'redux/contact/role/qa-unavailable/list/contactQAUnavailableRoleListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})