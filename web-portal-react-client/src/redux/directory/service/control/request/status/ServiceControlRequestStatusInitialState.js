import List from './list/ServiceControlRequestStatusListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})