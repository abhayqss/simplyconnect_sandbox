import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectService'

const fetch = (params) => service.findHouseholdMembers(params)

function useHouseholdMembersQuery(params, options) {
    return useQuery(['Prospect.HouseholdMembers', params], () => fetch(params), options)
}

export default useHouseholdMembersQuery