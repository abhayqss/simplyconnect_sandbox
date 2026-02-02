import {
	useEffect
} from 'react'

import {
	useAuthUser
} from 'hooks/common/redux'

import { useInNetworkCommunityExistsQuery } from '.'

import {
	SYSTEM_ROLES
} from 'lib/Constants'

const {
	CONTENT_CREATOR,
	SUPER_ADMINISTRATOR
} = SYSTEM_ROLES

export default function useCommunityFilterInitialization(
	{
		isSaved,
		changeFields,
		updateDefaultData
	} = {}
) {
	const user = useAuthUser()

	const {
		data: inNetworkCommunityExists
	} = useInNetworkCommunityExistsQuery({}, {
		staleTime: 0
	})

	useEffect(() => {
		if (user) {
			const changes = {}

			if ([
				CONTENT_CREATOR,
				SUPER_ADMINISTRATOR
			].includes(user.roleName)) {
				changes.includeMyCommunities = false
				changes.includeInNetworkCommunities = false
			} else if (inNetworkCommunityExists) {
				changes.includeMyCommunities = false
				changes.includeInNetworkCommunities = true
			} else {
				changes.includeMyCommunities = true
				changes.includeInNetworkCommunities = false
			}

			updateDefaultData(changes)
			if (!isSaved()) changeFields(changes)
		}
	}, [
		user,
		isSaved,
		changeFields,
		updateDefaultData,
		inNetworkCommunityExists
	])
}