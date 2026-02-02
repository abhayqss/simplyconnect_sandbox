import React, { memo } from 'react'

import { Logo } from 'components'
import AppStoreLink from 'components/AppStoreLink/AppStoreLink'
import GooglePlayLink from 'components/GooglePlayLink/GooglePlayLink'

import './SelfSignUp.scss'

const MOBILE_APP_APPSTORE_ID = "1584993343"
const MOBILE_APP_APPSTORE_NAME = "simplyconnect-app"
const MOBILE_APP_GOOGLE_PLAY_ID = "com.simplyhie.scm"

const SelfSignUp = () => {
    return (
        <div className="SelfSignUp">
            <Logo
                iconSize={76}
                className="SelfSignUp-LogoImage"
            />
            <div className="d-flex flex-column">
                <div className="d-flex flex-column">
                    <span className="SelfSignUp-Title margin-top-25">
                        Link has expired
                    </span>
                    <span className="SelfSignUp-InfoText">
                        The link has expired. Please resubmit your 
                        registration using the Simply Connect mobile app.
                    </span>
                </div>
                <div className="d-flex SelfSignUp-AppLinks">
                    <AppStoreLink appId={MOBILE_APP_APPSTORE_ID} appName={MOBILE_APP_APPSTORE_NAME} />
                    <GooglePlayLink appId={MOBILE_APP_GOOGLE_PLAY_ID} />
                </div>
            </div>
            
        </div>
    )
}

export default memo(SelfSignUp)
