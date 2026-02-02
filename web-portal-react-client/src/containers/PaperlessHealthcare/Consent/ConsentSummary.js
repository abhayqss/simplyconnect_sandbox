import React, { memo } from 'react'

import imageSrc from 'images/hws/consent-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function ConsentSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Consent"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/consent"
        >
            Consent management allows to seamlessly share your health information on a secure basis within your own personal care team.
            Consent management allows for a patient, resident or family (POA) power of attorney to OPTIN/OPTOUT.  So your natural supports can provide better care.
        </SectionSummary>
    )
}

export default memo(ConsentSummary)