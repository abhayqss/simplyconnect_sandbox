import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

const fetch = params => service.findCommunities(params)

export default function useDocumentESignCommunityQuery(params, options) {
    return useQuery(['DocumentESign.Communities', params], () => fetch(params), options)
}