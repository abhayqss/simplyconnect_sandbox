import { useMutation } from 'hooks/common'

import service from 'services/TransportationService'

const fetch = (params) => service.rideHistory(params, {
	response: { extractDataOnly: true }
})

export default function useTransportationRideHistoryQuery(params, options) {
	return useMutation(params, fetch, options)
}