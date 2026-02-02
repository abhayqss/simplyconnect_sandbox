import {
	all,
	map,
	where
} from 'underscore'

import {
	useQueryWatch
} from 'hooks/common'

import {
	isEmpty
} from 'lib/utils/Utils'

function mapToIds(data) {
	return map(data, o => o.id)
}

function filterCommunities(data) {
	return where(data, { canViewOrHasAccessibleClient: true })
}

export default function useProspectPrimaryFilterInitialization(
	{ organizationId, communityIds, changeFields } = {}
) {
	useQueryWatch({
		queryKey: ['Directory.Organizations', {
			checkCommunitiesExist: true,
			areAppointmentsEnabled: true
		}],
		onSuccess: data => {
			if (all(data, o => o.id !== organizationId)) {
				changeFields({
					communityIds: [],
					organizationId: data[0].id
				})
			}
		}
	})

	useQueryWatch({
		queryKey: ['Directory.Communities', { organizationId }],
		onSuccess: data => {
			const filteredCommunities = filterCommunities(data)

			changeFields({
				communityIds: (
					isEmpty(communityIds) ? mapToIds(filteredCommunities) : communityIds
				)
			})
		}
	})
}