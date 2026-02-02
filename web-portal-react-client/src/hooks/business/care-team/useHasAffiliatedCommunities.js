import { useQuery } from 'hooks/common'

import service from 'services/CareTeamMemberService'

const fetch = ({ clientId, ...params }) => (
    service.hasAffiliatedCommunities(clientId, params)
)

function useHasAffiliatedCommunities(params, options) {
    return useQuery('HasAffiliatedCommunities', params, {
        fetch,
        ...options,
    })
}

export default useHasAffiliatedCommunities
