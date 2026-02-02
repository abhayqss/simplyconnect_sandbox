import { useMemo } from 'react'

import { findWhere } from 'underscore'
import { useQueryClient } from '@tanstack/react-query'

import { isEqLn } from 'lib/utils/Utils'

export default function useReducedContactFilterData(data) {
    const {
        organizationId,
        systemRoleIds = [],
        statuses: statusNames = []
    } = data

    const queryClient = useQueryClient()

    const organizations = queryClient.getQueryData(
        ['Directory.Organizations', null]
    )

    const organization = findWhere(
        organizations, { id: organizationId }
    )

    const isExternal = organization?.label?.includes('External')

    const systemRoles = queryClient.getQueryData(
        ['Directory.SystemRoles', { includeExternal: isExternal }]
    )

    const areAllSystemRoles = isEqLn(systemRoles, systemRoleIds)

    const statuses = queryClient.getQueryData(
        ['Directory.ContactStatuses', null]
    )

    const areAllStatuses = isEqLn(statuses, statusNames)

    return useMemo(() => ({
        ...data,
        systemRoleIds: areAllSystemRoles ? [] : data.systemRoleIds,
        statuses: areAllStatuses ? [] : data.statuses
    }), [
        data,
        areAllSystemRoles,
        areAllStatuses
    ])
}