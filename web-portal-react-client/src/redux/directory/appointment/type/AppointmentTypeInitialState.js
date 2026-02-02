import List from './list/AppointmentTypeListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})