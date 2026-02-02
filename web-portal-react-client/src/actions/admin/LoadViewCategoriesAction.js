import Factory from '../ActionFactory'

import actions from 'redux/organization/category/can/view/canViewCategoriesActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})