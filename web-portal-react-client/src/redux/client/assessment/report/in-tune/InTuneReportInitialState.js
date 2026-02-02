import Can from './can/СanInTuneReportInitialState'
import Details from './details/InTuneReportDetailsInitialState'

const { Record } = require('immutable');

export default Record({
    can: Can(),
    details: Details(),
});