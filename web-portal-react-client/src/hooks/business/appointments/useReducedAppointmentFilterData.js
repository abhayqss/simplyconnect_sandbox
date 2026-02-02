import { useMemo } from 'react'

import { findWhere } from 'underscore'
import { useQueryClient } from '@tanstack/react-query'

import {
	useAuthUser
} from 'hooks/common/redux'

import {
    SYSTEM_ROLES,
    CONTACT_STATUSES
} from 'lib/Constants'

import { isEqLn, isNotEmpty } from 'lib/utils/Utils'

const {
    SUPER_ADMINISTRATOR
} = SYSTEM_ROLES

const {
    ACTIVE,
    INACTIVE
} = CONTACT_STATUSES

export default function useReducedAppointmentFilterData(data) {
    const {
        organizationId,
        communityIds = [],
        creatorIds = [],
        clientIds = [],
        serviceProviderIds = [],
        types: typeNames = [],
        statuses: statusNames = []
    } = data

    const user = useAuthUser()

    const queryClient = useQueryClient()

    const organizations = queryClient.getQueryData(
        ['Directory.Organizations', null]
    )

    const organization = findWhere(
        organizations, { id: organizationId }
    )

    const communities = queryClient.getQueryData(
        ['Directory.Communities', { organizationId }]
    )

    const areAllCommunities = isEqLn(communities, communityIds)

    const creators = queryClient.getQueryData(
        ['AppointmentContacts', {
            organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : organizationId,
            statuses: [ACTIVE, INACTIVE],
            withAccessibleCreatedAppointments: true
        }]
    )

    const areAllCreators = isNotEmpty(creators) && isEqLn(creators, creatorIds)

    const clients = queryClient.getQueryData(
        ['Directory.Clients', { organizationId, communityIds }]
    )

    const areAllClients = isNotEmpty(clients) && isEqLn(clients, clientIds)

    const serviceProviders = queryClient.getQueryData(
        ['AppointmentContacts', {
            organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : organizationId,
            statuses: [ACTIVE, INACTIVE],
            withAccessibleScheduledAppointments: true
        }]
    )

    const areAllServiceProviders = isNotEmpty(serviceProviders) && isEqLn(serviceProviders, serviceProviderIds)

    const types = queryClient.getQueryData(
        ['Directory.AppointmentTypes', { organizationId }]
    )

    const areAllTypes = isNotEmpty(types) && isEqLn(types, typeNames)

    const statuses = queryClient.getQueryData(
        ['Directory.AppointmentStatuses', { organizationId }]
    )

    const areAllStatuses = isNotEmpty(statuses) && isEqLn(statuses, statusNames)

    return useMemo(() => ({
        ...data,
        communityIds: areAllCommunities ? [] : data.communityIds,
        creatorIds: areAllCreators ? [] : data.creatorIds,
        clientIds: areAllClients ? [] : data.clientIds,
        serviceProviderIds: areAllServiceProviders ? [] : data.serviceProviderIds,
        types: areAllTypes ? [] : data.types,
        statuses: areAllStatuses ? [] : data.statuses,
    }), [
        data,
        areAllCommunities,
        areAllCreators,
        areAllClients,
        areAllServiceProviders,
        areAllTypes,
        areAllStatuses
    ])
}