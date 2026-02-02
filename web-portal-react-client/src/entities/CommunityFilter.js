const { Record, List } = require('immutable')

const CommunityFilter = Record({
	includeMyCommunities: false,
	includeInNetworkCommunities: false,
	serviceCategoryId: null,
	serviceIds: List([])
})

export default CommunityFilter