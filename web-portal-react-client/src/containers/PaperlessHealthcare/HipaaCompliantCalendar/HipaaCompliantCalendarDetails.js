import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function HipaaCompliantCalendarDetails() {

    return (
        <SectionDetails
            title="HIPAA-Compliant Calendar"
            name={FEATURES.HIPAA_COMPLIANT_CALENDAR}
            Icon={Icon}
            youtubeVideoId=""
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                HIPPA Secure schedule for your health needs. A central schedule for all
                activities including transportation, care and activities.
            </div>
        </SectionDetails>
    )
}

export default memo(HipaaCompliantCalendarDetails)