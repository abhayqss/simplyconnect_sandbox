import Type from './type/IncidentReportClassMemberTypeInitialState'

const { Record } = require('immutable')

export default Record({
    type: Type(),
})