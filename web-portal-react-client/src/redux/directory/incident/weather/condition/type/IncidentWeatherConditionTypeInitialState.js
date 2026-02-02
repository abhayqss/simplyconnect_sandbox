import List from './list/IncidentWeatherConditionTypeListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
})
