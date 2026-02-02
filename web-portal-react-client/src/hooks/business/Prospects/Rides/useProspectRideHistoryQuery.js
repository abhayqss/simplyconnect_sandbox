import { useQuery } from '@tanstack/react-query'

import service from 'services/TransportationService'

const fetch = (params) => service.rideHistory(params)

export default function useProspectRideHistoryQuery(params, options) {
	return useQuery(['Prospect.RideHistory', params], () => fetch(params), options)
}