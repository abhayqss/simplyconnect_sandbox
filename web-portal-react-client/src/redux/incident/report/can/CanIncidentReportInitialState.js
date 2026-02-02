import Add from './add/CanAddIncidentReportInitialState'
import View from './view/CanViewIncidentReportsInitialState'

const { Record } = require('immutable');

export default Record({
    add: Add(),
    view: View()
});