import Add from './add/CanAddAssessmentInitialState'
import View from './view/CanViewAssessmentsInitialState'

const { Record } = require('immutable');

export default Record({
    add: Add(),
    view: View()
});