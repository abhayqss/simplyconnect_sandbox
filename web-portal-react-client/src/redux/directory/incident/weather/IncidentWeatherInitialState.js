import Condition from './condition/IncidentWeatherConditionInitialState'

const { Record } = require('immutable')

export default Record({
    condition: Condition(),
})
