import React, { memo } from 'react'

import imageSrc from 'images/hws/hipaa-calendar-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function HipaaCompliantCalendarSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="HIPAA-Compliant Calendar"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/hipaa-compliant-calendar"
        >
            HIPAA Secure schedule for your health needs. A central schedule for
            all activities including transportation, care and activities.
        </SectionSummary>
    )
}

export default memo(HipaaCompliantCalendarSummary)