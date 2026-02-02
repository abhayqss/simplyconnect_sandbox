import Factory from '../ActionFactory'

import actions from 'redux/incident/report/can/view/canViewIncidentReportsActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})