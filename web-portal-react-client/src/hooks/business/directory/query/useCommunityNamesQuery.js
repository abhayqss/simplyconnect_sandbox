import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findCommunityNames(params)

function useCommunityNamesQuery(params, options) {
    return useQuery('CommunityName', params, {
        fetch,
        ...options,
    })
}

export default useCommunityNamesQuery
