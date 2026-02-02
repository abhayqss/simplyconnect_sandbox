import Factory from '../ActionFactory'

import actions from 'redux/directory/client/list/clientListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})