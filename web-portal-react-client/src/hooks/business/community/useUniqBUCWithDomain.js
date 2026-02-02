import { useMutation } from '@tanstack/react-query'

import service from 'services/CommunityService'

export default function useUniqBUCWithDomain(options) {
    return useMutation(({
        serverDomain,
        businessUnitCodes,
        excludeCommunityId
    }) => service.getNonUniqBusinessUnitCodes({
        serverDomain,
        businessUnitCodes,
        excludeCommunityId
    }), options)
}
