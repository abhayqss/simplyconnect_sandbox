import React, { memo } from 'react'

import imageSrc from 'images/hws/pharmacy-medications-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function PharmacyMedicationsSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Pharmacy / Medications"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/pharmacy-medications"
        >
            Access real-time medication information from your desktop or phone.
            Easily track new medications, changes or discontinued medications via direct alerts.
        </SectionSummary>
    )
}

export default memo(PharmacyMedicationsSummary)