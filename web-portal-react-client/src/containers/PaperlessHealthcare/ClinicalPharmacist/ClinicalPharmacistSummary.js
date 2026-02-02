import React, { memo } from 'react'

import imageSrc from 'images/hws/clinical-pharmacist-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function ClinicalPharmacistSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Clinical Pharmacist"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/clinical-pharmacist"
        >
            Clinical pharmacists are licensed for medication reconciliation, deprescribe,
            navigation oversight for your medication intake. A piece of mind knowing you have a
            licensed clinical pharmacist helping you understand prescription medications, formulary,
            assisting your care team for fall reductions and more.
        </SectionSummary>
    )
}

export default memo(ClinicalPharmacistSummary)