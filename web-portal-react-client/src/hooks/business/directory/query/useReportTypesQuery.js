import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findReportTypes(params)

function useReportTypesQuery(params, options) {
    return useQuery('ReportTypes', params, {
        fetch, ...options
    })
}

export default useReportTypesQuery
