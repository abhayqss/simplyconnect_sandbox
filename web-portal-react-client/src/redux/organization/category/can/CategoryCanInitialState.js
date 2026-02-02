import Add from './add/CanAddCategoriesInitialState'
import View from './view/CanViewCategoriesInitialState'

const { Record } = require('immutable');

export default Record({
    add: Add(),
    view: View()
})