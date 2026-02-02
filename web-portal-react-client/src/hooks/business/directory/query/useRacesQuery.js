import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = (params) => service.findRaces(
    params, { response: { extractDataOnly: true } }
)

export default function useRacesQuery(params, options) {
    return useQuery(['Directory.Races', params], () => fetch(params), options)
}
