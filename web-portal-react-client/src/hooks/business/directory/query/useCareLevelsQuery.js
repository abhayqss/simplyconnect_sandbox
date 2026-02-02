import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = () => service.findCareLevels()

function useCareLevelsQuery(params, options) {
    return useQuery('CareLevels', params, {
        fetch,
        ...options,
    })
}

export default useCareLevelsQuery
