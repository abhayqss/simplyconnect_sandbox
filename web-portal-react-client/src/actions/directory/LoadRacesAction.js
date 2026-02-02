import Factory from '../ActionFactory'

import actions from 'redux/directory/race/list/raceListActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})
