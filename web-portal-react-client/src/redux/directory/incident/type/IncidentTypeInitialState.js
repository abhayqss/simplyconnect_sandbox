import List from './list/IncidentTypeListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
})