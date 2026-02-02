import * as actions from 'redux/community/details/communityDetailsActions'

import Factory from '../ActionFactory'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})