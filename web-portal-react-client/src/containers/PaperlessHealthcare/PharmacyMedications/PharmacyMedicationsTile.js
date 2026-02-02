import React, {
    memo,
    useCallback
} from 'react'

import { useHistory } from 'react-router-dom'

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
    FEATURES,
    SYSTEM_ROLES
} from 'lib/Constants'

import { first } from 'lib/utils/ArrayUtils'
import { path } from 'lib/utils/ContextUtils'

import imageSrc from 'images/hws/pharmacy-medications.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { PHARMACY_MEDICATIONS } = FEATURES

const { PERSON_RECEIVING_SERVICES } = SYSTEM_ROLES

function PharmacyMedicationsTile({ theme, onClick }) {
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

    const _onClick = useCallback(name => {
        if (clientCount === 1) {
            history.push(
                path(`/clients/${first(clients)?.id}#medications-summary`)
            )
        } else if (user?.roleName === PERSON_RECEIVING_SERVICES) {
            onClick(name)
        } else if (clientCount > 1) {
            history.push(path('/clients'), {
                isMedicationsInstructionNeed: true
            })
        } else {
            history.push(path('/clients'), {
                isAddingOrAccessRecordsAndMedicationsInstructionNeed: true
            })
        }
    }, [
        user,
        history,
        clients,
        clientCount,
        onClick
    ])

    return (
        <SectionTile
            name={PHARMACY_MEDICATIONS}
            title="Pharmacy / Medications"
            imageSrc={imageSrc}
            onClick={_onClick}
        />
    )
}

export default memo(PharmacyMedicationsTile)