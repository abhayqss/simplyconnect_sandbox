import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

function fetch(params) {
	return service.canViewNotes(params)
}

export default function useCanViewNotesQuery(params, options) {
	return useQuery(['Client.CanViewNotes', params], () => fetch(params), options)
}