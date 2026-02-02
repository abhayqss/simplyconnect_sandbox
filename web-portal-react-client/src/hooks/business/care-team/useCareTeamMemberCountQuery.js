import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = (params) => service.count(
	params, { response: { extractDataOnly: true } }
)

export default function useCareTeamMemberCountQuery(params, options) {
	return useQuery(['CareTeamMember.Count', params], () => fetch(params), options)
}