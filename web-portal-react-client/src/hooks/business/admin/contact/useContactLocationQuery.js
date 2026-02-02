import { useQuery } from '@tanstack/react-query'

import service from 'services/ContactService'

const fetch = ({ contactId, ...params }) => service.findLocationById(
	contactId, params
)

export default function useContactLocationQuery(params, options) {
	return useQuery(['Contact.Location', params], () => fetch(params), options)
}