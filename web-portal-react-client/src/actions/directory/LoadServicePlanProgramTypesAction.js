import Factory from '../ActionFactory'

import * as actions from 'redux/directory/servicePlan/program/type/list/programTypeListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})