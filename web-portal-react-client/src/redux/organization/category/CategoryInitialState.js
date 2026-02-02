import Can from './can/CategoryCanInitialState'
import List from './list/CategoryListInitialState'

const { Record } = require('immutable');

export default Record({
    can: Can(),
    list: List()
})