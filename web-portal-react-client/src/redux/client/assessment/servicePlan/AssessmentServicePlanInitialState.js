import NeedIdentification from './needIdentification/AssessmentServicePlanNeedIdentificationInitialState'

const { Record } = require('immutable')

export default Record({
    needIdentification: NeedIdentification()
})