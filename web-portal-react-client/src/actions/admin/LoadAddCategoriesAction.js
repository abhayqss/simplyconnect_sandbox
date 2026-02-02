import Factory from '../ActionFactory'

import actions from 'redux/organization/category/can/add/canAddCategoriesActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})