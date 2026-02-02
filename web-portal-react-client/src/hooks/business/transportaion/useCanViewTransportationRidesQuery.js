import { useQuery } from '@tanstack/react-query'

import service from 'services/TransportationService'

const fetch = params => service.canView(params)

export default function useCanViewTransportationRidesQuery(params, options) {
    return useQuery(['Transportation.Rides.CanView', params], () => fetch(params), options)
}