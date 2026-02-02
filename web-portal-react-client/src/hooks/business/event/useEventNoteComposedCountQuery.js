import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

const fetch = (params) => service.count(
	params, { response: { extractDataOnly: true } }
)

export default function useEventNoteComposedCountQuery(params, options) {
	return useQuery(['EventNoteComposedCount', params], () => fetch(params), options)
}