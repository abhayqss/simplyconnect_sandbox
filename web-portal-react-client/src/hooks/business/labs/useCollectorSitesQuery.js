import { useQuery } from 'hooks/common'

import service from 'services/LabResearchOrderService'

const fetch = params => service.findCollectorSites(params)

function useCollectorSitesQuery(params, options) {
    return useQuery('CollectorSite', params, {
        fetch,
        ...options,
    })
}

export default useCollectorSitesQuery