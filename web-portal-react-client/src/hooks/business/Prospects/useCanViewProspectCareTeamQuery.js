import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = (params) => service.canView(params, {
    response: { extractDataOnly: true }
})

function useCanViewProspectCareTeamQuery(params, options) {
    return useQuery(['Prospect.CareTeam.CanView', params], () => fetch(params), options)
}

export default useCanViewProspectCareTeamQuery