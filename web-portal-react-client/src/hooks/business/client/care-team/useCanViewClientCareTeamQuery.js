import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = params => service.canView(
	params, { response: { extractDataOnly: true } }
)

export default function useCanViewClientCareTeamQuery(params, options) {
	return useQuery(['Client.CareTeam.CanView', params], () => fetch(params), options)
}