import Type from './type/AppointmentTypeInitialState'
import Status from './status/AppointmentStatusInitialState'

const { Record } = require('immutable')

export default Record({
    type: Type(),
    status: Status(),
})