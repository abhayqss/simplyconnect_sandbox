import List from './list/ClientAllergyListInitialState'
import Count from './count/ClientAllergyCountInitialState'
import Details from './details/ClientAllergyDetailsInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    count: Count(),
    details: Details()
})