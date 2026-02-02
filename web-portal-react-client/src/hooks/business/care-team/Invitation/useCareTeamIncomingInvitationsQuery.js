import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = ({ communityId, ...params }) => service.findCareTeamIncomingInvitations(
    communityId, params
)

function useCareTeamIncomingInvitationsQuery(params, options) {
    return useQuery(['CareTeamMember.IncomingInvitations', params], () => fetch(params), options)
}

export default useCareTeamIncomingInvitationsQuery
