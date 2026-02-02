import { useMutation } from 'hooks/common'

import service from 'services/PrivateMarketplaceCommunityService'

function submit({ communityId } = {}) {
	return service.removeById(communityId)
}

function useCommunityRemoving(params, options) {
	return useMutation(params, submit, options)
}

export default useCommunityRemoving
