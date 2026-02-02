import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = params => service.count(params, {
    response: { extractDataOnly: true }
})

function useClientCareTeamMemberCountQuery(params, options) {
    return useQuery(['Client.CareTeam.MemberCount', params], () => fetch(params), options)
}

export default useClientCareTeamMemberCountQuery
