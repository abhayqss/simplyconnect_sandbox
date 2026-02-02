import React, { memo } from 'react'

import imageSrc from 'images/hws/care-team-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function TeamCareSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Team Care"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/team-care"
        >
            Team Care allows multiple approved natural supports to come together and
            provide seamless communications. Better aligned care sharing health
            information & communications takes the hassle out of your healthcare journey.
        </SectionSummary>
    )
}

export default memo(TeamCareSummary)