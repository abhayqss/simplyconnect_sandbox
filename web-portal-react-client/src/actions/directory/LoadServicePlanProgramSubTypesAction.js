import Factory from '../ActionFactory'

import * as actions from 'redux/directory/servicePlan/program/subtype/list/programSubTypeListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})