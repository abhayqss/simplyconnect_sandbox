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

import imageSrc1 from 'images/hws/personal-dashboard.jpg'
import imageSrc2 from 'images/hws/personal-dashboard-2.jpg'

import { THEMES } from '../Constants/Constants'

import SectionTile from '../components/SectionTile/SectionTile'

const {
    PERSON_RECEIVING_SERVICES
} = SYSTEM_ROLES

const { PERSONAL_DASHBOARD } = FEATURES

const { BLUE, DARK_BLUE } = THEMES

const THEME_IMAGES = {
    [BLUE]: imageSrc1,
    [DARK_BLUE]: imageSrc2
}

function getImageSrc(theme) {
    return THEME_IMAGES[theme] || imageSrc1
}

function PersonalHealthRecord({ theme, onClick }) {
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
            history.push(path(`/clients/${first(clients).id}`))
        } else if (
            clientCount > 1
            || user?.roleName !== PERSON_RECEIVING_SERVICES
        ) {
            history.push(path(`/clients`), {
                isIntroductionNeed: clientCount === 0
            })
        } else onClick(name)
    }, [
        user,
        history,
        clients,
        onClick,
        clientCount
    ])

    return (
        <SectionTile
            name={PERSONAL_DASHBOARD}
            title="Personal Dashboard"
            theme={theme}
            imageSrc={getImageSrc(theme)}
            onClick={_onClick}
        />
    )
}

export default memo(PersonalHealthRecord)