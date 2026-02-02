import List from './list/ServiceListInitialState'
import Status from './status/ServiceStatusInitialState'
import Control from './control/ServiceControlInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    status: Status(),
    control: Control()
})