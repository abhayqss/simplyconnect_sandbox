import React, { memo } from 'react'

import { Button } from 'reactstrap'

import {
    useNotifyLoginForm,
    useCanLoginToNotify
} from 'hooks/business/notify'

import imageSrc from 'images/hws/notify-fall-detection-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function NotifyFallDetectionSummary({ onClose, onDemo }) {
    const canLogin = useCanLoginToNotify()

    const [submitLoginForm, loginForm] = useNotifyLoginForm()

    return (
        <SectionSummary
            title="Notify Fall Detection"
            imageSrc={imageSrc}
            onClose={onClose}
            moreInfoPath="paperless-healthcare/notify-fall-detection"
            actions={() => (
                <>
                    <Button
                        color="success"
                        onClick={onDemo}
                        className="btn-size-regular SectionSummary-Action"
                    >
                        Get a Demo
                    </Button>
                    {canLogin && (
                        <Button
                            color="success"
                            onClick={submitLoginForm}
                            className="SectionSummary-Action"
                        >
                            Login to Notify
                        </Button>
                    )}
                </>
            )}
        >
            Staff are notified of a potential resident incident in their apartment.
            <ul className="padding-top-10">
                <li>24/7 alerts for incidents like fall or inactivity.</li>
                <li>Automated alert if resident is out of reach of a smartphone, pendant or pull cord.</li>
                <li>Integrates into your nurse call system.</li>
            </ul>
            Safety & comfort are a priority amongst prospects and family in choosing a community.
            {loginForm}
        </SectionSummary>
    )
}

export default memo(NotifyFallDetectionSummary)