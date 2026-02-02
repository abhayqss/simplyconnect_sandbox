import React, { memo } from 'react'

import imageSrc from 'images/hws/communications-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function CommunicationsSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Communications"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/communications"
        >
            Real-time secure communications via voice, video, text and push-to-talk with native IOS and Android.
        </SectionSummary>
    )
}

export default memo(CommunicationsSummary)