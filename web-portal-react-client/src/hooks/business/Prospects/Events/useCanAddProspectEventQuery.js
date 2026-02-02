import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

const fetch = (params) => service.canAdd(params)

export default function useCanAddProspectEventQuery(params, options) {
	return useQuery(['Prospect.CanAddEvent', params], () => fetch(params), options)
}