import List from './list/ContactStatusListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})