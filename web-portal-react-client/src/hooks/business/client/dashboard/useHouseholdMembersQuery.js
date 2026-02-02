import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientService'

const fetch = params => service.findHouseholdMembers(params)

function useHouseholdMembersQuery(params, options) {
    return useQuery(['Client.HouseholdMembers', params], () => fetch(params), options)
}

export default useHouseholdMembersQuery