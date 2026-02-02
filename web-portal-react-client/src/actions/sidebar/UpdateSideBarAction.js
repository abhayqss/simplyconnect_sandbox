import Factory from '../ActionFactory'

import * as actions from 'redux/sidebar/sideBarActions'

export default Factory(actions, {
    action: ({ changes }, actions) => actions.update(changes)
})