import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findCommunities(
    params, { response: { extractDataOnly: true } }
)

export default function useCommunitiesQuery(params, options) {
    return useQuery(['Directory.Communities', params], () => fetch(params), options)
}