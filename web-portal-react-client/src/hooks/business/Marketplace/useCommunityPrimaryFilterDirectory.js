import {	
    first
} from 'underscore'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useCommunitiesQuery,
    useOrganizationsQuery
} from 'hooks/business/directory/query'

import {
	isEmpty,
    isInteger
} from 'lib/utils/Utils'

export default function useCommunityPrimaryFilterDirectory(
	{ organizationId, communityId } = {},
	{ actions: changeCommunityField } = {}
) {
	const user = useAuthUser()

	const {
		data: organizations = []
	} = useOrganizationsQuery(null, {
		staleTime: 0,
	})

	const {
		data: communities = []
	} = useCommunitiesQuery(
		{
			organizationId
		},
		{
			staleTime: 0,
			enabled: isInteger(organizationId),
			onSuccess: (data) => {
				if (isEmpty(data)) return 

				const isUserCommunityApplicable = 
					user?.organizationId === organizationId && !!user?.communityId

				const newCommunityId = isUserCommunityApplicable ? user?.communityId : first(data).id

				if (!communityId) {
					changeCommunityField(newCommunityId)
				}
			}
		}
	)

	return { organizations, communities }
}
