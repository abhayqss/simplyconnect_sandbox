import React, {
    memo,
    useCallback
} from 'react'

import { useHistory } from 'react-router-dom'

import { useAuthUser } from 'hooks/common/redux'

import { FEATURES } from 'lib/Constants'
import { path } from 'lib/utils/ContextUtils'

import imageSrc from 'images/hws/communications.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { COMMUNICATIONS } = FEATURES

function CommunicationsTile({ onClick }) {
    const user = useAuthUser()
    const history = useHistory()

    const _onClick = useCallback(name => {
        if (user?.areConversationsEnabled) {
            history.push(path('/chats'))
        } else onClick(name)
    }, [
        user,
        history,
        onClick
    ])

    return (
        <SectionTile
            name={COMMUNICATIONS}
            title="Communications"
            imageSrc={imageSrc}
            onClick={_onClick}
        />
    )
}

export default memo(CommunicationsTile)