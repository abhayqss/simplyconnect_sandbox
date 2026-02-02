import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import { useSelector } from 'react-redux'

import { ErrorViewer } from 'components'

import { useAuthUser } from 'hooks/common/redux'
import { useTracking } from 'hooks/business/tracking'
import { useConversations } from 'hooks/business/conversations'

export default function Authentication() {
    const [error, setError] = useState(null)

    const user = useAuthUser()

    const {
        catchError,
        catchMessage
    } = useTracking()

    const {
        isReady
    } = useSelector(state => state.conversations)

    const session = useSelector(state => state.auth.session)

    const {
        on, off, updateToken
    } = useConversations()

    const onTokenAboutToExpire = useCallback(() => {
        if (user) updateToken().catch(e => {
            catchError(e)
            catchMessage(`Twilio token is about to expire. Backend access token error.`)
            catchMessage(`isReady: ${isReady}. Auth User: ${JSON.stringify(user)}`)
            throw e
        })
    }, [user, isReady, updateToken, catchError, catchMessage])

    useEffect(() => {
        if (user && session.isAboutToExpire) {
            updateToken().then(() => {
                catchMessage(`Forced Access token. Fetch success!`)
            }).catch(e => {
                catchError(e)
                catchMessage(`Forced Access token. Fetch failure!`)
                catchMessage(`isReady: ${isReady}. Auth User: ${JSON.stringify(user)}`)
                throw e
            })
        }
    }, [user, isReady, session, updateToken, catchError, catchMessage])

    useEffect(() => {
        if (user && isReady) {
            on('tokenAboutToExpire', onTokenAboutToExpire)

            return () => {
                off('tokenAboutToExpire', onTokenAboutToExpire)
            }
        }
    }, [on, off, user, isReady, onTokenAboutToExpire])

    return (
        <>
            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}