import {
    map,
    noop,
    chain,
    findWhere
} from 'underscore'

import { useQueryClient } from '@tanstack/react-query'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useSystemRolesQuery,
    useContactStatusesQuery
} from 'hooks/business/directory/query'

import { CONTACT_STATUSES } from 'lib/Constants'

function mapToIds(data) {
    return map(data, o => o.id)
}

const {
    ACTIVE,
    PENDING
} = CONTACT_STATUSES

export default function useContactFilterDirectory(
    { organizationId } = {},
    {
        actions: {
            isFilterSaved = noop(),
            changeFilterFields = noop(),
            updateFilterDefaultData = noop()
        }
    }
) {
    const user = useAuthUser()

    const isDefaultOrganization = (
        organizationId === user?.organizationId
    )

    const queryClient = useQueryClient()

    const organizations = queryClient.getQueryData(
        ['Directory.Organizations', null]
    )

    const organization = findWhere(
        organizations, { id: organizationId }
    )

    const {
        data: systemRoles
    } = useSystemRolesQuery(
        {
            organizationId,
            includeExternal: (
                organization?.label?.includes('External')
            )
        },
        {
            staleTime: 0,
            onSuccess: data => {
                const changes = {
                    systemRoleIds: mapToIds(data),
                    includeWithoutSystemRole: true
                }

                updateFilterDefaultData(changes)

                if (!isFilterSaved()) changeFilterFields(
                    changes, isDefaultOrganization
                )
            }
        }
    )

    const {
        data: statuses
    } = useContactStatusesQuery(
        { organizationId },
        {
            staleTime: 0,
            onSuccess: data => {
                const changes = {
                    statuses: chain(data)
                        .filter(s => [ACTIVE, PENDING].includes(s.name))
                        .map(s => s.name)
                        .value()
                }

                updateFilterDefaultData(changes)

                if (!isFilterSaved()) changeFilterFields(
                    changes, isDefaultOrganization
                )
            }
        }
    )

    return { systemRoles, statuses }
}