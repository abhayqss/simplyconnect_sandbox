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
    useCanViewDocumentsQuery
} from 'hooks/business/documents'

import {
    useClientCountQuery
} from 'hooks/business/client/queries'

import {
    FEATURES,
    SYSTEM_ROLES
} from 'lib/Constants'

import { first } from 'lib/utils/ArrayUtils'
import { path } from 'lib/utils/ContextUtils'

import imageSrc from 'images/hws/documents.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { DOCUMENTS } = FEATURES

const {
    PERSON_RECEIVING_SERVICES
} = SYSTEM_ROLES

function DocumentsTile({ onClick }) {
    const user = useAuthUser()
    const history = useHistory()

    const {
        data: canView = false
    } = useCanViewDocumentsQuery()

    const {
        data: clientCount = 0
    } = useClientCountQuery()

    const {
        data: clients = []
    } = useClientsQuery({}, {
        enabled: clientCount === 1
    })

    const _onClick = useCallback(name => {
        if (user?.roleName === PERSON_RECEIVING_SERVICES) {
            if (clientCount === 1) {
                history.push(
                    path(`/clients/${first(clients)?.id}/documents`),
                    { isInstructionNeed: true }
                )
            } else onClick(name)
        } else {
            if (canView) {
                history.push(path(`/company-documents`), {
                    isInstructionNeed: true
                })
            } else if (clientCount === 1) {
                history.push(
                    path(`/clients/${first(clients)?.id}/documents`),
                    { isManagementInstructionNeed: true }
                )
            } else if (clientCount > 1) {
                history.push(path('/clients'), {
                    isDocumentManagementInstructionNeed: true
                })
            } else {
                history.push(path('/clients'), {
                    isAddingOrAccessRecordsAndDocumentManagementInstructionNeed: true
                })
            }
        }
    }, [
        user,
        history,
        canView,
        clients,
        clientCount,
        onClick
    ])

    return (
        <SectionTile
            name={DOCUMENTS}
            title="Documents & e-Sign"
            imageSrc={imageSrc}
            onClick={_onClick}
        />
    )
}

export default memo(DocumentsTile)