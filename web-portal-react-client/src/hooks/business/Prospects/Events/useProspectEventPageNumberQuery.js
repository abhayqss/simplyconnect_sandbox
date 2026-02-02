import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectEventService'

const fetch = (params) => service.findPageNumber(params)

export default function useProspectEventPageNumberQuery(params, options) {
	return useQuery(['Prospect.EventPageNumber', params], () => fetch(params), options)
}