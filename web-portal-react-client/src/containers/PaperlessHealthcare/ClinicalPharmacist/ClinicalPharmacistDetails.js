import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function ClinicalPharmacistDetails() {

    return (
        <SectionDetails
            title="Clinical Pharmacist"
            Icon={Icon}
            name={FEATURES.CLINICAL_PHARMACIST}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                Clinical pharmacists are licensed for medication reconciliation, predescribe, navigation
                oversight for your medication intake. A piece of mind knowing you have a licensed clinical pharmacist
                helping you understand prescription medications, formulary, assisting your care team for fall reductions and more.
            </div>
        </SectionDetails>
    )
}

export default memo(ClinicalPharmacistDetails)