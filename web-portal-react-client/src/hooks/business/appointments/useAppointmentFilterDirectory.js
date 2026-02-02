import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useClientsQuery,
    useClientStatusesQuery,
    useAppointmentTypesQuery,
    useAppointmentStatusesQuery
} from 'hooks/business/directory/query'

import {
    useAppointmentContacts
} from 'hooks/business/appointments'

import {
    SYSTEM_ROLES,
    CLIENT_STATUSES,
    CONTACT_STATUSES
} from 'lib/Constants'

import {
    isNumber,
    isInteger
} from 'lib/utils/Utils'

const { ACTIVE } = CLIENT_STATUSES
const { INACTIVE } = CONTACT_STATUSES
const { SUPER_ADMINISTRATOR } = SYSTEM_ROLES

export default function useAppointmentFilterDirectory(
    {
        communityIds,
        clientStatuses: clientStatusNames,
        organizationId,
        clientsWithAccessibleAppointments
    } = {}
) {
    const user = useAuthUser()

    const {
        data: creators = []
    } = useAppointmentContacts({
            statuses: [ACTIVE, INACTIVE],
            organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : organizationId,
            withAccessibleCreatedAppointments: true
        }, {
            staleTime: 0,
            enabled: !!user && isNumber(organizationId)
        }
    )

    const {
        data: clients = [],
        isFetching: isFetchingClients
    } = useClientsQuery({
        communityIds,
        organizationId,
        recordStatuses: clientStatusNames,
        withAccessibleAppointments: clientsWithAccessibleAppointments
    }, {
        staleTime: 0,
        enabled: !!user && isInteger(organizationId)
    })

    const {
        data: clientStatuses = []
    } = useClientStatusesQuery(
        { organizationId }, { staleTime: 0 }
    )

    const {
        data: serviceProviders = []
    } = useAppointmentContacts({
        statuses: [ACTIVE, INACTIVE],
        organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : organizationId,
        withAccessibleScheduledAppointments: true
    }, {
        staleTime: 0,
        enabled: !!user && isNumber(organizationId)
    })

    const {
        data: types = []
    } = useAppointmentTypesQuery(
        { organizationId }, { staleTime: 0 }
    )

    const {
        data: statuses = []
    } = useAppointmentStatusesQuery(
        { organizationId }, { staleTime: 0 }
    )

    return {
        types,
        clients,
        creators,
        statuses,
        clientStatuses,
        serviceProviders,
        isFetchingClients
    }
}