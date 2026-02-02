import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectService'

const fetch = (params) => service.findEmergencyContacts(params)

function useEmergencyContactsQuery(params, options) {
    return useQuery(['Prospect.EmergencyContacts', params], () => fetch(params), options)
}

export default useEmergencyContactsQuery