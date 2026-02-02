import Date from './date/OldestIncidentReportDateInitialState'

const { Record } = require('immutable')

export default Record({
    date: Date()
})