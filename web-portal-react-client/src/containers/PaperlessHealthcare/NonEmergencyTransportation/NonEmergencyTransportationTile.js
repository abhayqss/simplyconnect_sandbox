import React, {
    memo,
    useCallback
} from 'react'

import { useHistory } from 'react-router-dom'

import {
    useClientsQuery
} from 'hooks/business/directory/query'

import {
    useClientCountQuery
} from 'hooks/business/client/queries'

import {
    useCanViewTransportationRidesQuery
} from 'hooks/business/transportaion'

import { FEATURES } from 'lib/Constants'

import { first } from 'lib/utils/ArrayUtils'
import { path } from 'lib/utils/ContextUtils'

import imageSrc from 'images/hws/non-emergency-transportation.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { NON_EMERGENCY_TRANSPORTATION } = FEATURES

function NonEmergencyTransportationTile({ onClick }) {
    const history = useHistory()

    const {
        data: canView
    } = useCanViewTransportationRidesQuery()

    const {
        data: clientCount = 0
    } = useClientCountQuery()

    const {
        data: clients = []
    } = useClientsQuery({}, {
        enabled: clientCount === 1
    })

    const _onClick = useCallback(name => {
        if (canView) {
            if (clientCount === 1) {
                history.push(
                    path(`/clients/${first(clients)?.id}/rides`),
                    { isInstructionNeed: true }
                )
            } else if (clientCount > 1) {
                history.push(path('/clients'), {
                    isRidesInstructionNeed: true
                })
            } else {
                history.push(path('/clients'), {
                    isAddingOrAccessRecordsAndRidesInstructionNeed: true
                })
            }
        } else onClick(name)
    }, [
        history,
        clients,
        canView,
        clientCount,
        onClick
    ])

    return (
        <SectionTile
            name={NON_EMERGENCY_TRANSPORTATION}
            title="Non-Emergency Transportation"
            imageSrc={imageSrc}
            onClick={_onClick}
        />
    )
}

export default memo(NonEmergencyTransportationTile)