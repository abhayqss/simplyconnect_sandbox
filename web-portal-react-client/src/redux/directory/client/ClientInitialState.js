import List from './list/ClientListInitialState'
import Status from './status/ClientStatusInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    status: Status()
})