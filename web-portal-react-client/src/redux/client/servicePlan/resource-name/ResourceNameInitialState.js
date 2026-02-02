import List from './list/ResourceNameListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
})