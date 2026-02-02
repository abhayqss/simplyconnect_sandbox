import { useQuery } from '@tanstack/react-query'

import service from 'services/TransportationService'

const fetch = (params) => service.rideRequest(params)

export default function useProspectRideRequestQuery(params, options) {
	return useQuery(['Prospect.RideRequest', params], () => fetch(params), options)
}