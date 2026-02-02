import React, { memo } from 'react'

import imageSrc from 'images/hws/referral-marketplace-2.jpg'

import SectionSummary from '../components/SectionSummary/SectionSummary'

function ReferralMarketplaceSummary({ onClose, onDemo }) {
    return (
        <SectionSummary
            title="Referral Marketplace"
            imageSrc={imageSrc}
            onClose={onClose}
            onDemo={onDemo}
            moreInfoPath="paperless-healthcare/referral-marketplace"
        >
            A searchable marketplace for paperless referrals,
            keeping your brand top of mind for patients and families.
        </SectionSummary>
    )
}

export default memo(ReferralMarketplaceSummary)