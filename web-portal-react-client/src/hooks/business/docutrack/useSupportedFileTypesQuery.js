import { useQuery } from '@tanstack/react-query'

import service from 'services/DocuTrackService'

const fetch = params => service.findSupportedFileTypes(params)

export default function useSupportedFileTypesQuery(params, options) {
    return useQuery(['DocuTrack.SupportedFileTypes', params], () => fetch(params), options)
}