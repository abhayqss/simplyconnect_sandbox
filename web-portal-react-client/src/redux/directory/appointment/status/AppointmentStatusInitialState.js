import List from './list/AppointmentStatusListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})