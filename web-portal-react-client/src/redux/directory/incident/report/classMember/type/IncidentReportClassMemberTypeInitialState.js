import List from './list/IncidentReportClassMemberListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
})