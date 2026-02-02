import {
	useRacesQuery,
	useStatesQuery,
	useGendersQuery,
	useCommunitiesQuery,
	useOrganizationsQuery,
	useMaritalStatusesQuery,
	useInsuranceNetworksQuery,
	useRelatedPartyRelationshipTypesQuery
} from 'hooks/business/directory/query'

import {
	PAGINATION
} from 'lib/Constants'

import {
	isInteger
} from 'lib/utils/Utils'

const { MAX_SIZE } = PAGINATION

export default function useProspectFormDirectory({ organizationId }) {
	const {
		data: genders = []
	} = useGendersQuery({}, { staleTime: 0 })

	const {
		data: maritalStatuses = []
	} = useMaritalStatusesQuery({}, { staleTime: 0 })

	const {
		data: races = []
	} = useRacesQuery({}, { staleTime: 0 })

	const {
		data: states = []
	} = useStatesQuery({}, { staleTime: 0 })

	const {
		data: { data: networks = [] } = {},
		isFetching: isFetchingInsuranceNetworks
	} = useInsuranceNetworksQuery({ size: MAX_SIZE })

	const {
		data: organizations = [],
		isFetching: isFetchingOrganizations
	} = useOrganizationsQuery({
		checkCommunitiesExist: true
	}, { staleTime: 0 })

	const {
		data: communities = []
	} = useCommunitiesQuery(
		{ organizationId },
		{
			enabled: isInteger(organizationId),
			staleTime: 0
		}
	)

	const {
		data: relationshipTypes = []
	} = useRelatedPartyRelationshipTypesQuery({}, { staleTime: 0 })

	return {
		races,
		states,
		genders,
		networks,
		communities,
		organizations,
		maritalStatuses,
		relationshipTypes,
		isFetchingOrganizations,
		isFetchingInsuranceNetworks
	}
}