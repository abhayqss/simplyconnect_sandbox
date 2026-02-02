import { useMutation } from 'hooks/common'

import service from 'services/PrivateMarketplaceCommunityService'

function submit({ communityId } = {}) {
	return service.saveById(communityId)
}

function useCommunitySaving(params, options) {
	return useMutation(params, submit, options)
}

export default useCommunitySaving
