import {
    map,
    noop,
    flatten
} from 'underscore'

import { useAuthUser } from 'hooks/common/redux'

import {
    useClientsQuery,
    useContactsQuery,
    useCommunitiesQuery,
    useOrganizationsQuery,
    useActivityTypesQuery
} from 'hooks/business/directory/query'

import {
    isInteger,
    isNotEmpty
} from 'lib/utils/Utils'

import { useOldestAuditLogDateQuery } from './index'

const CACHE_CONFIG = {
    cacheTime: 0,
    staleTime: 3 * 60 * 1000
}

export default function useAuditLogFilterDirectory(
    { organizationId, communityIds } = {},
    {
        actions: {
            isFilterSaved = noop(),
            changeFilterField = noop(),
            updateFilterDefaultData = noop()
        }
    } = {}
) {

    const user = useAuthUser()

    const isDefaultOrganization = (
        organizationId === user?.organizationId
    )

    const {
        data: organizations
    } = useOrganizationsQuery({}, {
        retry: 1,
        ...CACHE_CONFIG
    })

    const {
        data: communities
    } = useCommunitiesQuery({ organizationId }, {
        ...CACHE_CONFIG,
        enabled: isInteger(organizationId),
        onSuccess: data => {
            const communityIds = map(data, o => o.id)

            updateFilterDefaultData({ communityIds })
            !isFilterSaved() && changeFilterField(
                'communityIds', communityIds, isDefaultOrganization
            )
        }
    })

    const areAllCommunities = (
        communityIds?.length === communities?.length
    )

    const isDefaultOrganizationAndAllCommunities = (
        isDefaultOrganization && areAllCommunities
    )

    const {
        data: contacts
    } = useContactsQuery({ communityIds, organizationId }, {
        staleTime: 0,
        enabled: isInteger(organizationId) && isNotEmpty(communityIds),
        onSuccess: data => {
            const employeeIds = map(data, o => o.id)

            if (isDefaultOrganizationAndAllCommunities) {
                updateFilterDefaultData({ employeeIds })
            }

            !isFilterSaved() && changeFilterField(
                'employeeIds', employeeIds, isDefaultOrganizationAndAllCommunities
            )
        }
    })

    const {
        data: activityTypes
    } = useActivityTypesQuery({ organizationId }, {
        ...CACHE_CONFIG,
        onSuccess: data => {
            const activityIds = flatten(
                map(data, o => map(o.activities, o => o.id))
            )

            updateFilterDefaultData({ activityIds })
            !isFilterSaved() && changeFilterField(
                'activityIds', activityIds, isDefaultOrganization
            )
        }
    })

    const {
        data: clients
    } = useClientsQuery({ communityIds, organizationId }, {
        staleTime: 0,
        enabled: isInteger(organizationId) && isNotEmpty(communityIds),
        onSuccess: data => {
            const clientIds = map(data, o => o.id)

            if (isDefaultOrganizationAndAllCommunities) {
                updateFilterDefaultData({ clientIds })
            }

            !isFilterSaved() && changeFilterField(
                'clientIds', clientIds, isDefaultOrganizationAndAllCommunities
            )
        }
    })

    const {
        data: oldestAuditLogDate,
        isFetching: isFetchingOldestAuditLogDate
    } = useOldestAuditLogDateQuery({ organizationId }, {
        ...CACHE_CONFIG,
        enabled: isInteger(organizationId),
        onSuccess: date => {
            updateFilterDefaultData({ fromDate: date })
            !isFilterSaved() && changeFilterField(
                'fromDate', date, isDefaultOrganization
            )
        }
    })

    return {
        organizations,
        communities,
        contacts,
        activityTypes,
        clients,
        oldestAuditLogDate,
        isFetchingOldestAuditLogDate
    }
}