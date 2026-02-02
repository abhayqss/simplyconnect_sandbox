import List from './list/contactListInitialState'
import System from './system/SystemInitialState'
import Status from './status/ContactStatusInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    system: System(),
    status: Status()
})