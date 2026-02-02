import React, { memo } from 'react'

import imageSrc from 'images/hws/personal-dashboard-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function PersonalDashboardSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Personal Dashboard"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/personal-dashboard"
        >
            A personal health dashboard (PHD) is a health dashboard where health data and
            other information related to a person’s care is maintained. The intention of a
            PHD is to provide a complete and accurate summary of an individual's medical
            history which is accessible real-time online or on their phones. The health data
            on a PHD might include demographics, medications, lab results, chronic conditions,
            immunizations, and data from sources.
        </SectionSummary>
    )
}

export default memo(PersonalDashboardSummary)