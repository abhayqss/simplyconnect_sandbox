import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = (params) => service.canView(
	params, { response: { extractDataOnly: true } }
)

export default function useCanViewCareTeamMemberQuery(params, options) {
	return useQuery(['CareTeamMember.CanView', params], () => fetch(params), options)
}