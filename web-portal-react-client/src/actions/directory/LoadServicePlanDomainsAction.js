import Factory from '../ActionFactory'

import * as actions from 'redux/directory/servicePlan/domain/list/domainListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})