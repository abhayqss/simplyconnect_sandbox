import Can from './can/СanServicePlanInitialState'
import List from './list/ServicePlanListInitialState'
import Form from './form/ServicePlanFormInitialState'
import Count from './count/ServicePlanCountInitialState'
import Details from './details/ServicePlanDetailsInitialState'
import History from './history/ServicePlanHistoryInitialState'
import Controlled from './controlled/ControlledInitialState'
import ResourceName from './resource-name/ResourceNameInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    can: Can(),
    form: Form(),
    list: List(),
    count: Count(),
    details: Details(),
    history: History(),
    controlled: Controlled(),
    resourceName: ResourceName(),
})

export default InitialState