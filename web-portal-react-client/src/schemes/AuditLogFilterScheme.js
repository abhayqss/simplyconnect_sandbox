import {
    Shape,
    ListOf,
    integer
} from './types'

const AuditLogFilterScheme = Shape({
    organizationId: integer().required(),
    communityIds: ListOf(integer()).required(),
    employeeIds: ListOf(integer()).required(),
    activityIds: ListOf(integer()).required(),
    fromDate: integer().required(),
    toDate: integer().required(),
})

export default AuditLogFilterScheme
