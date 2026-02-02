import Date from './date/LatestIncidentReportDateInitialState'

const { Record } = require('immutable')

export default Record({
    date: Date()
})