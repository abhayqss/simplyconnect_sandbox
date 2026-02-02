import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function PharmacyMedicationsDetails() {

    return (
        <SectionDetails
            title="Pharmacy / Medications"
            Icon={Icon}
            name={FEATURES.PHARMACY_MEDICATIONS}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                Access real-time medication information from your desktop or phone.
                Easily track new medications, changes or discontinued medications via direct alerts.
            </div>
        </SectionDetails>
    )
}

export default memo(PharmacyMedicationsDetails)