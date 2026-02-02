import { useQuery } from '@tanstack/react-query'

import service from 'services/CareTeamMemberService'

const fetch = (params) => service.canAdd(params, {
    response: { extractDataOnly: true }
})

function useCanAddCareTeamMemberQuery(params, options) {
    return useQuery(['CanAddCareTeamMember', params], () => fetch(params), options)
}

export default useCanAddCareTeamMemberQuery