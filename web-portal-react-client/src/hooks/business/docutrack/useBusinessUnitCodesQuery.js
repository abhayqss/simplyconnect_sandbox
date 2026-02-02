import { useQuery } from '@tanstack/react-query'

import service from 'services/DocuTrackService'

const fetch = params => service.findBusinessUnitCodes(params)

export default function useBusinessUnitCodesQuery(params, options) {
    return useQuery(['DocuTrack.BusinessUnitCodes', params], () => fetch(params), options)
}