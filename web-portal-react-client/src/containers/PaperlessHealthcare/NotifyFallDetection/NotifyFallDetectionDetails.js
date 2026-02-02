import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function NotifyFallDetectionDetails() {

    return (
        <SectionDetails
            title="Notify Fall Detection"
            Icon={Icon}
            name={FEATURES.NOTIFY_FALL_DETECTION}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                Staff are notified of a potential resident incident in their apartment.
                <ul>
                    <li>24/7 alerts for incidents like fall or inactivity.</li>
                    <li>Automated alert if resident is out of reach of a smartphone, pendant or pull cord.</li>
                    <li>Integrates into your nurse call system.</li>
                </ul>
                Safety & comfort are a priority amongst prospects and family in choosing a community.
            </div>
        </SectionDetails>
    )
}

export default memo(NotifyFallDetectionDetails)