import Factory from '../ActionFactory'

import * as actions from 'redux/directory/care/level/list/careLevelListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})