import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = () => service.findAgeGroups()

function useAgeGroupsQuery(params, options) {
    return useQuery('AgeGroups', params, {
        fetch,
        ...options,
    })
}

export default useAgeGroupsQuery
