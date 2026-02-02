import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import SectionDetails from '../components/SectionDetails/SectionDetails'

import { FEATURES } from 'lib/Constants'

function NonEmergencyTransportationDetails() {

    return (
        <SectionDetails
            name={FEATURES.NON_EMERGENCY_TRANSPORTATION}
            title="Transportation"
            Icon={Icon}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                Easily schedule non-emergency transportation services and share
                details with family members and care teams.
            </div>
        </SectionDetails>
    )
}

export default memo(NonEmergencyTransportationDetails)