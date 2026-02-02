import List from './list/ClientAssessmentListInitialState'
import Statistics from './statistics/ClientAssessmentStatisticsInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    statistics: Statistics()
})