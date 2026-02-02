import Report from './report/IncidentReportInitialState'
import Picture from './picture/IncidentPictureInitialState'

const { Record } = require('immutable')

export default Record({
    report: Report(),
    picture: Picture()
})