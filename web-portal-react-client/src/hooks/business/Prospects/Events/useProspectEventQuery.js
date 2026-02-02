import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectEventService'

const fetch = ({ eventId, ...params }) => service.findById(eventId, params)

export default function useProspectEventQuery(params, options) {
	return useQuery(['Prospect.Event', params], () => fetch(params), options)
}