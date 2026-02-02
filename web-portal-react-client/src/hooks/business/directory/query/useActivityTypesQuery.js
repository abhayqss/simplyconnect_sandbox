import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findActivityTypes(
    params, { response: { extractDataOnly: true } }
)

function useActivityTypesQuery(params, options) {
    return useQuery(['Directory.ActivityTypes', params], () => fetch(params), options)
}

export default useActivityTypesQuery
