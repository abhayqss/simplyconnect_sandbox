import React, { memo } from 'react'

import imageSrc from 'images/hws/documents-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function DocumentsSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Documents & e-Sign"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/documents"
        >
            Static documents with e sign within team Care is A HIPAA-secure platform that
            supports paperless healthcare, enhances efficiency and streamlines power of attorney.
        </SectionSummary>
    )
}

export default memo(DocumentsSummary)