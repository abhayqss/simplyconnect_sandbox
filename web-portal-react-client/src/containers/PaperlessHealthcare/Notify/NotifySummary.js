import React, { memo } from 'react'

import { Button } from 'reactstrap'

import {
    useNotifyLoginForm,
    useCanLoginToNotify
} from 'hooks/business/notify'

import imageSrc from 'images/hws/notify-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function NotifySummary({ onClose, onDemo }) {
    const canLogin = useCanLoginToNotify()

    const [submitLoginForm, loginForm] = useNotifyLoginForm()

    return (
        <SectionSummary
            title="NOTIFY"
            imageSrc={imageSrc}
            onClose={onClose}
            moreInfoPath="paperless-healthcare/notify"
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
            Immediate actions are taken with Notify take app and real time alerts with
            staffing care teams and communications. Voice, video, text and push to talk.
            As a new resident you can begin communicating with staffing on a real time basis.
            {loginForm}
        </SectionSummary>
    )
}

export default memo(NotifySummary)