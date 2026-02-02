import List from './list/SavedMarketplaceCommunityListInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: List(),
})

export default InitialState