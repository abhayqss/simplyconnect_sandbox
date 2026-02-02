import Factory from '../ActionFactory'

import * as actions from 'redux/directory/primaryFocus/list/primaryFocusListActions'

export default Factory(actions, {
    action: ({ shouldDispatch, ...params } = {}, actions) => (
        actions.load(params, shouldDispatch)
    )
})