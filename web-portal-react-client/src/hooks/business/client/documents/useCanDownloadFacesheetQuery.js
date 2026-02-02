import { useQuery } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

const fetch = params => service.canDownloadFacesheet(params)

function useCanDownloadFacesheetQuery(params, options) {
    return useQuery(['Client.CanDownloadFacesheet', params], () => fetch(params), options)
}

export default useCanDownloadFacesheetQuery