import { useQuery } from '@tanstack/react-query'

import service from 'services/EventNoteService'

const fetch = (params) => service.findNewestDate(
    params, { response: { extractDataOnly: true } }
)

export default function useNewestEventDateQuery(params, options) {
    return useQuery(['NewestEventDate', params], () => fetch(params), options)
}