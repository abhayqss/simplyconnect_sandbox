import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = params => service.careTeamEmployeeCount(params)

function useClientCareTeamContactCountQuery(params, options) {
    return useQuery(['Client.CareTeam.ContactCount', params], () => fetch(params), options)
}

export default useClientCareTeamContactCountQuery
