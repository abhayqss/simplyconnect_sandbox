import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

const fetch = params => service.canDownloadCCD(params)

function useCanDownloadCCDQuery(params, options) {
    return useQuery(['Client.CanDownloadCCD', params], () => fetch(params), options)
}

export default useCanDownloadCCDQuery