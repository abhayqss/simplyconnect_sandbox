import List from './list/ClientProblemListInitialState'
import Details from './details/ClientProblemDetailsInitialState'
import Statistics from './statistics/ClientProblemStatisticsInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    details: Details(),
    statistics: Statistics(),
})