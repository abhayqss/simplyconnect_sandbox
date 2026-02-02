import Factory from '../ActionFactory'

import actions from 'redux/directory/system/role/list/systemRoleListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})
