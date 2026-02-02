import React, {
    memo,
    useCallback
} from 'react'

import {
    useHistory
} from 'react-router-dom'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useClientsQuery
} from 'hooks/business/directory/query'

import {
    useClientCountQuery
} from 'hooks/business/client/queries'

import {
    useClientCareTeamMemberCountQuery,
    useClientCareTeamContactCountQuery
} from 'hooks/business/client/care-team'

import {
    FEATURES,
    SYSTEM_ROLES
} from 'lib/Constants'

import { first } from 'lib/utils/ArrayUtils'
import { path } from 'lib/utils/ContextUtils'

import imageSrc from 'images/hws/care-team.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const {
    PERSON_RECEIVING_SERVICES
} = SYSTEM_ROLES

const { TEAM_CARE } = FEATURES

function TeamCareTile({ onClick }) {
    const user = useAuthUser()
    const history = useHistory()

    const {
        data: clientCount = 0
    } = useClientCountQuery()

    const {
        data: clients = []
    } = useClientsQuery({}, {
        enabled: clientCount === 1
    })

    const client = first(clients)

    const {
        data: memberCount
    } = useClientCareTeamMemberCountQuery(
        { clientId: client?.id },
        {
            staleTime: 0,
            enabled: clientCount === 1 && Boolean(client)
        }
    )

    const {
        data: contactCount
    } = useClientCareTeamContactCountQuery(
        {
            clientId: client?.id,
            organizationId: user?.organizationId
        },
        {
            staleTime: 0,
            enabled: (
                Boolean(user)
                && Boolean(client)
                && clientCount === 1
            )
        }
    )

    const _onClick = useCallback(name => {
        if (user?.roleName === PERSON_RECEIVING_SERVICES) {
            if (clientCount === 1) {
                const state = {}

                if (memberCount === 0) {
                    state.isAddingMembersInstructionNeed = contactCount > 0
                    state.isCommunityStaffContactNeed = contactCount === 0
                }

                history.push(path(`/clients/${client?.id}/care-team`), state)
            } else onClick(name)
        } else {
            if (clientCount === 1) {
                history.push(path(`/clients/${client?.id}/care-team`))
            } else {
                history.push(path(`/clients`), {
                    isCareTeamManagementInstructionNeed: clientCount > 1,
                    isAddingOrAccessRecordsInstructionNeed: clientCount === 0
                })
            }
        }
    }, [
        user,
        client,
        history,
        memberCount,
        clientCount,
        contactCount,
        onClick
    ])

    return (
        <SectionTile
            name={TEAM_CARE}
            title="Team Care"
            imageSrc={imageSrc}
            onClick={_onClick}
        />
    )
}

export default memo(TeamCareTile)