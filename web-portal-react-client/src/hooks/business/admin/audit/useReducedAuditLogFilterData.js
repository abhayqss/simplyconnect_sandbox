import { useMemo } from 'react'

import { map, flatten } from 'underscore'
import { useQueryClient } from '@tanstack/react-query'

export default function useReducedAuditLogFilterData(data) {
    const {
        communityIds = [],
        organizationId = [],
        activityIds = [],
        employeeIds = [],
        clientIds = [],
    } = data

    const queryClient = useQueryClient()

    const communitiesQuery = queryClient.getQuery(
        ['Directory.Communities', { organizationId }]
    ) ?? []

    const communities = communitiesQuery?.state?.data?.data ?? []
    const areAllCommunities = communities.length === communityIds.length

    const contacts = queryClient.getQueryData(
        ['Directory.Contacts', { organizationId, communityIds }]
    )

    const areAllContacts = contacts?.length === employeeIds.length

    const clients = queryClient.getQueryData(
        ['Directory.Clients', { organizationId, communityIds }]
    )

    const areAllClients = clients?.length === clientIds.length

    const allActivityIds = flatten(map(
        queryClient.getQueryData(['Directory.ActivityTypes', { organizationId }]),
        o => map(o.activities, o => o.id)
    )) ?? []

    const areAllActivities = allActivityIds.length === activityIds.length

    return useMemo(() => ({
        ...data,
        communityIds: areAllCommunities ? [] : data.communityIds,
        activityIds: areAllActivities ? [] : data.activityIds,
        employeeIds: areAllContacts ? [] : data.employeeIds,
        clientIds: areAllClients ? [] : data.clientIds
    }), [
        data,
        areAllCommunities,
        areAllActivities,
        areAllContacts,
        areAllClients
    ])
}