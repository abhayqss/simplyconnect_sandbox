import Factory from '../ActionFactory'

import actions from 'redux/audit/log/can/view/canViewAuditLogsActions'

export default Factory(actions, {
    action: (params, actions) => actions.load(params)
})