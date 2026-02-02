import List from './list/ServiceStatusListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})