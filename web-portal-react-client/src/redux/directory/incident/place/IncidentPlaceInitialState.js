import List from './list/IncidentPlaceListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
})