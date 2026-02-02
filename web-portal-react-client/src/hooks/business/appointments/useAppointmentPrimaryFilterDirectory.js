import {
	useMemo
} from 'react'

import {
	where
} from 'underscore'

import {
	useAuthUser
} from 'hooks/common/redux'

import {
	useCommunitiesQuery,
	useOrganizationsQuery
} from 'hooks/business/directory/query'

import {
	isInteger
} from 'lib/utils/Utils'

function filterCommunities(data) {
	return where(data, { canViewOrHasAccessibleClient: true })
}

export default function useAppointmentPrimaryFilterDirectory(
    { organizationId } = {}
) {
	const user = useAuthUser()

	const {
		data: organizations = []
	} = useOrganizationsQuery({
		areAppointmentsEnabled: true,
		checkCommunitiesExist: true
	}, {
		staleTime: 0,
		enabled: Boolean(user)
	})

	const {
		data: communities = []
	} = useCommunitiesQuery(
		{ organizationId },
		{
			staleTime: 0,
			enabled: (
				Boolean(user)
				&& isInteger(organizationId)
			)
		}
	)

	const filteredCommunities = useMemo(
		() => filterCommunities(communities),
		[communities]
	)

	return { organizations, communities: filteredCommunities }
}