import Status from './status/IncidentReportStatusInitialState'
import ClassMember from './classMember/IncidentReportClassMemberInitialState'

const { Record } = require('immutable')

export default Record({
    status: Status(),
    classMember: ClassMember(),
})
