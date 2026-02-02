import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

const fetch = (params) => service.findOldestDate(
    params, { response: { extractDataOnly: true } }
)

export default function useOldestEventDateQuery(params, options) {
    return useQuery(['OldestEventDate', params], () => fetch(params), options)
}