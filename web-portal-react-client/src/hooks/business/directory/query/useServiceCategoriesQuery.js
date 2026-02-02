import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findServiceCategories(params)

function useServiceLanguagesQuery(params, options) {
    return useQuery(['Directory.ServiceCategories', params], () => fetch(params), options)
}

export default useServiceLanguagesQuery
