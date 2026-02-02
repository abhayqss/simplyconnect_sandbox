import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = ({ clientId, ...params }) => service.careTeamIncomingInvitationsExist(
    clientId, params
)

function useCareTeamIncomingInvitationsExistQuery(params, options) {
    return useQuery(['Client.CareTeamMember.IncomingInvitationsExists', params], () => fetch(params), options)
}

export default useCareTeamIncomingInvitationsExistQuery
