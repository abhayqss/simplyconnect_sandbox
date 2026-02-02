import React, { memo, useEffect } from 'react'

import { useSelector } from 'react-redux'

import cn from 'classnames'

import {
    useQueryRemoving,
    useQueryInvalidation
} from 'hooks/common'

import { Loader } from 'components'

import Conversations from 'containers/Communication/Messenger/Conversations/Conversations'

import './Messenger.scss'

function Messenger({ className }) {
    const user = useSelector(
        state => state.auth.login.user.data
    )

    const isReady = useSelector(
        state => state.conversations.isReady
    )

    const removeQueries = useQueryRemoving()

    useEffect(() => () => {
        removeQueries('Conversations')
    }, [removeQueries])

    let isLoading = !user || !isReady

    if (isLoading) return <Loader />

    return (
        <div className={cn('Messenger', className)}>
            <Conversations className="Messenger-Conversations" />
        </div>
    )
}

export default memo(Messenger)