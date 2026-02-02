import List from './list/CommunityListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})