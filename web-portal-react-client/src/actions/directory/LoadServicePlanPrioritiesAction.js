import Factory from '../ActionFactory'

import * as actions from 'redux/directory/servicePlan/priority/list/priorityListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})