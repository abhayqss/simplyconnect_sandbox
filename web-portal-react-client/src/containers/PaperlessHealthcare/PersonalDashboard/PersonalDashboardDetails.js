import React, { memo } from 'react'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function PersonalDashboardDetails() {

    return (
        <SectionDetails
            title="Personal Dashboard"
            youtubeVideoId="TU2WHN_EoBU"
            name={FEATURES.PERSONAL_DASHBOARD}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                A personal health dashboard (PHD) is a health dashboard where
                health data and other information related to a person’s care is maintained.
                The intention of a PHD is to provide a complete and accurate summary of an individual's
                medical history which is accessible real-time online or on their phones. The health data
                on a PHD might include demographics, medications, lab results, chronic conditions, immunizations,
                and data from sources.
            </div>
        </SectionDetails>
    )
}

export default memo(PersonalDashboardDetails)