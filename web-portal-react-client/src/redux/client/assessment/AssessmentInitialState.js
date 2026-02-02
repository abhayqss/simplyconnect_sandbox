import Can from './can/СanAssessmentInitialState'
import List from './list/AssessmentListInitialState'
import Form from './form/AssessmentFormInitialState'
import Count from './count/AssessmentCountInitialState'
import Details from './details/AssessmentDetailsInitialState'
import History from './history/AssessmentHistoryInitialState'
import Default from './default/AssessmentDefaultDataInitialState'
import AnyInProcess from './anyInProcess/IsAnyAssessmentInProcessInitialState'

import Report from './report/ReportInitialState'
import ServicePlan from './servicePlan/AssessmentServicePlanInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    form: Form(),
    count: Count(),
    details: Details(),
    history: History(),
    report: Report(),
    default: Default(),
    servicePlan: ServicePlan(),
    anyInProcess: AnyInProcess()
})