import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

const fetch = params => service.findNoteContacts(params)

function useNoteContactsQuery(params, options) {
    return useQuery(['Notes.Contacts', params], () => fetch(params), options)
}

export default useNoteContactsQuery
