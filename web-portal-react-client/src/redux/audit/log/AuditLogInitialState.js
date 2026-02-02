import Can from './can/CanAuditLogsInitialState'
import List from './list/AuditLogListInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    can: Can(),
    list: List()
})

export default InitialState