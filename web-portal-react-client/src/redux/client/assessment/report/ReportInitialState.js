import InTune from './in-tune/InTuneReportInitialState'

const { Record } = require('immutable');

export default Record({
    inTune: InTune()
});