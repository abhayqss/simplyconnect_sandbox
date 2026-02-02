import Type from './type/IncidentWeatherConditionTypeInitialState'

const { Record } = require('immutable')

export default Record({
    type: Type(),
})
