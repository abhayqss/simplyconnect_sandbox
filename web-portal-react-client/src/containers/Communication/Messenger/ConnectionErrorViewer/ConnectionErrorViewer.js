import React, {
    useState,
    useEffect
} from 'react'

import { useSelector } from 'react-redux'
import { useRouteMatch } from 'react-router-dom'

import { ErrorDialog } from 'components/dialogs'

export default function ConnectionErrorViewer() {
    const [isOpen, toggle] = useState(false)

    const status = useSelector(
        state => state.conversations.connectionStatus
    )

    const match = useRouteMatch({ path: '*/chats' })

    useEffect(() => {
        if (match && status === 'disconnected') {
            toggle(true)
        }
    }, [status, match])

    return isOpen && (
        <ErrorDialog
            isOpen
            title="No internet connection"
            buttons={[{ text: 'Close', onClick: () => toggle(false) }]}
        >
            Your message has not been delivered. Please
            check your internet connection and try again
        </ErrorDialog>
    )
}