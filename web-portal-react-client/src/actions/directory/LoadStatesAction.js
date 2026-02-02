import Factory from '../ActionFactory'

import actions from 'redux/directory/state/list/stateListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load()
})