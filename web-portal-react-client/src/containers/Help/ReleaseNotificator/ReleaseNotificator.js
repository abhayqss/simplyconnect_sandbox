import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { map, find } from 'underscore'

import {
    useHistory,
    useLocation
} from 'react-router-dom'

import { Dialog } from 'components/dialogs'

import { useAuthUser } from 'hooks/common/redux'

import {
    AUTHENTICATION_EXCLUDED_PATHS
} from 'lib/Constants'

import { matches } from 'lib/utils/UrlUtils'
import { path } from 'lib/utils/ContextUtils'
import authUserStore from 'lib/stores/AuthUserStore'

import { ReactComponent as Dot } from 'images/dot.svg'

import './ReleaseNotificator.scss'

function clearNotifications() {
    authUserStore.save({
        ...authUserStore.get(), notifications: []
    })
}

function ReleaseNotificator() {
    const user = useAuthUser()
    const history = useHistory()
    const location = useLocation()

    const [isDialogOpen, setDialogOpen] = useState(false)

    const isNotExcludedPath = (
        AUTHENTICATION_EXCLUDED_PATHS.every(
            t => !matches(t, location.pathname)
        )
    )

    const notification = find(user?.notifications, n => (
        n.type === 'RELEASE'
    ))

    const onClose = useCallback(() => {
        clearNotifications()
        setDialogOpen(false)
    }, [])

    const onViewReleaseNotes = useCallback(() => {
        clearNotifications()
        setDialogOpen(false)
        history.push(path('/help/release-notes'))
    }, [history])

    useEffect(() => {
        if (user && notification && isNotExcludedPath) {
            setDialogOpen(true)
        } else {
            setDialogOpen(false)
        }
    }, [user, notification, isNotExcludedPath])

    return isDialogOpen && (
        <Dialog
            isOpen
            title={notification?.title}
            className="ReleaseNotificator"
            buttons={[
                {
                    text: 'Close',
                    color: 'outline-success',
                    onClick: onClose
                },
                {
                    text: "View Full Release Notes",
                    onClick: onViewReleaseNotes
                }
            ]}
        >
            <div className="ReleaseNotificationDetails">
                {notification?.body?.features && (
                    <div className="ReleaseNotificationDetails-Section">
                        <div className="ReleaseNotificationDetails-SectionTitle">
                            What's New:
                        </div>
                        <div className="ReleaseNotificationDetails-SectionBody">
                            {map(notification.body.features.split(/\n/), feature => (
                                <div key={feature}>
                                    <Dot className="ReleaseNotificationDetails-Marker"/>
                                    <span className="align-middle">{feature}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
                {notification?.body?.fixes && (
                    <div className="ReleaseNotificationDetails-Section">
                        <div className="ReleaseNotificationDetails-SectionTitle">
                            Bug Fixes:
                        </div>
                        <div className="ReleaseNotificationDetails-SectionBody">
                            {map(notification.body.fixes.split(/\n/), fix => (
                                <div key={fix}>
                                    <Dot className="ReleaseNotificationDetails-Marker"/>
                                    <span className="align-middle">{fix}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </Dialog>
    )
}

export default memo(ReleaseNotificator)