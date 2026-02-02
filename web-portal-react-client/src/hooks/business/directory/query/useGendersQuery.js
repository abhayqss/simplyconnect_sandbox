import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findGenders(
    params, { response: { extractDataOnly: true } }
)

export default function useGendersQuery(params, options) {
    return useQuery(['Directory.Genders', params], () => fetch(params), options)
}
