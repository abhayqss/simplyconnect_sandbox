import { State } from 'redux/utils/Form'
import ServicePlan from 'entities/ServicePlan'

export default State({
    tab: 0,
    error: null,
    isValid: true,
    isFetching: false,
    fields: ServicePlan()
})