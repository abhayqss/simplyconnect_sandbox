import { Actions } from 'redux/utils/Value'

import service from 'services/AuditLogService'

import actionTypes from './canViewAuditLogsActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params) => service.canView(params)
})