import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import SectionDetails from '../components/SectionDetails/SectionDetails'
import { FEATURES } from 'lib/Constants'

function ReferralMarketplaceDetails() {

    return (
        <SectionDetails
            title="Referral Marketplace"
            Icon={Icon}
            name={FEATURES.REFERRAL_MARKETPLACE}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                A searchable marketplace for paperless referrals, keeping your
                brand top of mind for patients and families.
            </div>
        </SectionDetails>
    )
}

export default memo(ReferralMarketplaceDetails)