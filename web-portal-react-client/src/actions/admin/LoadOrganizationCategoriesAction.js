import Factory from '../ActionFactory'

import actions from 'redux/organization/category/list/categoryListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})