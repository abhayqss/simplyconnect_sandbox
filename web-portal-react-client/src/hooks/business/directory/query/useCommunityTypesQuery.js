import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findCommunityTypes(params)

function useCommunityTypesQuery(params, options) {
    return useQuery('CommunityTypes', params, {
        fetch,
        ...options,
    })
}

export default useCommunityTypesQuery
