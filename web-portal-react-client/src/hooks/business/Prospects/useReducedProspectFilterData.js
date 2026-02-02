import { useMemo } from 'react'

import { useQueryClient } from '@tanstack/react-query'

import { isEqLn } from 'lib/utils/Utils'

export default function useReducedProspectFilterData(data) {
    const {
        organizationId,
        communityIds = []
    } = data

    const client = useQueryClient()

    const communities = client.getQueryData(
        ['Directory.Communities', { organizationId }]
    )

    const areAllCommunities = isEqLn(communities, communityIds)

    return useMemo(() => ({
        ...data,
        communityIds: areAllCommunities ? [] : data.communityIds
    }), [
        data,
        areAllCommunities
    ])
}