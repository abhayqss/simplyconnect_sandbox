import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function NotifyDetails() {

    return (
        <SectionDetails
            title="NOTIFY"
            Icon={Icon}
            name={FEATURES.NOTIFY}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                Immediate actions are taken with Notify take app and real time alerts with
                staffing care teams and communications. Voice, video, text and push to talk. As
                a new resident you can begin communicating with staffing on a teal time basis.
            </div>
        </SectionDetails>
    )
}

export default memo(NotifyDetails)