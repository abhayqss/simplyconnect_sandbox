import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

function fetch(params) {
	return service.canViewEventsAndNotes(params)
}

export default function useCanViewEventsAndNotesQuery(params, options) {
	return useQuery(['Client.CanViewEventsAndNotes', params], () => fetch(params), options)
}