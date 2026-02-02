import React from 'react'

import { useParams } from 'react-router-dom'

import DocumentTitle from 'react-document-title'

import { Logo } from 'components'

import { ReactComponent as DownloadAppStore } from 'images/download-app-store.svg'
import { ReactComponent as DownloadGooglePlay } from 'images/download-google-play.svg'

import './CareTeamInvitation.scss'

function CareTeamInvitation() {
    const { result } = useParams()

    const isExpired = result === 'expired'
    const isCancelled = result === 'cancelled'
    const isAccepted = result === 'accept'

    const shouldSignIn = !isExpired && !isCancelled && !isAccepted

    return (
        <DocumentTitle title="Simply Connect | Care Team Invitation">
            <div className="CareTeamInvitation">
                <div className="CareTeamInvitation-Body">
                    <Logo
                        iconSize={76}
                        className="CareTeamInvitation-LogoImage"
                    />
                    <div className="d-flex flex-column">
                        <span className="CareTeamInvitation-Title">
                            {isExpired && "Link has been expired"}
                            {isCancelled && "Invitation has been cancelled"}
                            {isAccepted && "Please create an account using the Simply Connect mobile app"}
                            {shouldSignIn && "Please sign in using the Simply Connect mobile app"}
                        </span>
                        <span className="CareTeamInvitation-InfoText">
                            {isExpired && "The link has been expired. Please contact the Sender to resend the invitation."}
                            {isCancelled && "The care team invitation has been cancelled by Sender."}
                        </span>
                        {(isAccepted || shouldSignIn) && (
                            <div className="d-flex">
                                <a
                                    href="https://apps.apple.com/app/simplyconnect-app/id1584993343"
                                    target="_blank"
                                    className="CareTeamInvitation-Download"
                                >
                                    <DownloadAppStore />
                                </a>
                                <a
                                    href="https://play.google.com/store/apps/details?id=com.simplyhie.scm&hl=en&gl=US"
                                    target="_blank"
                                    className="CareTeamInvitation-Download"
                                >
                                    <DownloadGooglePlay />
                                </a>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </DocumentTitle>
    )
}

export default CareTeamInvitation