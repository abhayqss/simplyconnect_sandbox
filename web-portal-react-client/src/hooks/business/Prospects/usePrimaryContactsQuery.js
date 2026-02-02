import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectService'

const fetch = (params) => service.findPrimaryContacts(params)

export default function usePrimaryContactsQuery(params, options) {
	return useQuery(['ProspectPrimaryContacts', params], () => fetch(params), options)
}