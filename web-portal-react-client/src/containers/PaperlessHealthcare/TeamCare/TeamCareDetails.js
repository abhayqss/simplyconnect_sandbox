import React, { memo } from 'react'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function TeamCareDetails() {

    return (
        <SectionDetails
            title="Team Care"
            name={FEATURES.TEAM_CARE}
            youtubeVideoId="WROcDmepUPY"
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                Team Care allows multiple approved natural supports to come together
                and provide seamless communications. Better aligned care sharing health
                information & communications takes the hassle out of your healthcare journey.
            </div>
        </SectionDetails>
    )
}

export default memo(TeamCareDetails)