import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import { Shape, integer, ListOf, bool } from './types'

const { EMPTY_FIELD } = VALIDATION_ERROR_TEXTS

const AffiliateRelationship = Shape({
    areAllPrimaryCommunitiesSelected: bool(),
    areAllAffiliatedCommunitiesSelected: bool(),

    primaryCommunities: ListOf().when(
        ['areAllPrimaryCommunitiesSelected'],
        (areAllPrimaryCommunitiesSelected, scheme) => areAllPrimaryCommunitiesSelected ? scheme : scheme.min(1, EMPTY_FIELD)
    ),
    affiliatedOrganization: Shape({
        id: integer().required()
    }),
    affiliatedCommunities: ListOf().when(
        ['areAllAffiliatedCommunitiesSelected'],
        (areAllAffiliatedCommunitiesSelected, scheme) => areAllAffiliatedCommunitiesSelected ? scheme : scheme.min(1, EMPTY_FIELD)
    ),
})

const OrganizationAffiliateRelationshipScheme = Shape({
    affiliatedRelationships: ListOf(AffiliateRelationship)
})

export default OrganizationAffiliateRelationshipScheme
