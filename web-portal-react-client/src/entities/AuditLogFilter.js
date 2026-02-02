const { Record } = require('immutable')

const AuditLogFilter = Record({
    organizationId: null,
    communityIds: [],
    employeeIds: [],
    activityIds: [],
    clientIds: [],
    fromDate: null,
    toDate: null,
})

export default AuditLogFilter
