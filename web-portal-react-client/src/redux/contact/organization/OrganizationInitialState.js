import List from './list/OrganizationListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})