import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

const fetch = params => service.count(params, {
	response: { extractDataOnly: true }
})

function useClientEventNoteComposedCountQuery(params, options) {
	return useQuery(['Client.Event.Note.ComposedCount', params], () => fetch(params), options)
}

export default useClientEventNoteComposedCountQuery