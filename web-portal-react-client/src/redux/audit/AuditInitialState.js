import Log from './log/AuditLogInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    log: Log()
})

export default InitialState