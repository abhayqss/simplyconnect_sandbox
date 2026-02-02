const { Record, List } = require('immutable')

const CommunityFilter = Record({
	serviceCategoryId: null,
	serviceIds: List([])
})

export default CommunityFilter