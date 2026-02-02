import React, { memo } from 'react'

import imageSrc from 'images/hws/non-emergency-transportation-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function NonEmergencyTransportationSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Non-Emergency Transportation"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/non-emergency-transportation"
        >
            Easily schedule non-emergency transportation services
            and share details with family members and care teams.
        </SectionSummary>
    )
}

export default memo(NonEmergencyTransportationSummary)