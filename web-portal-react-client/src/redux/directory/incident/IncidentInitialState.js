import Type from './type/IncidentTypeInitialState'
import Place from './place/IncidentPlaceInitialState'
import Level from './level/IncidentLevelInitialState'
import Report from './report/IncidentReportInitialState'
import Weather from './weather/IncidentWeatherInitialState'

const { Record } = require('immutable')

export default Record({
    type: Type(),
    place: Place(),
    level: Level(),
    report: Report(),
    weather: Weather(),
})