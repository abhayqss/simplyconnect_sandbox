import Factory from '../ActionFactory'

import actions from 'redux/client/assessment/default/assessmentDefaultDataActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})