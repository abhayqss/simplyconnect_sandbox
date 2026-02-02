import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function CommunicationsDetails() {

    return (
        <SectionDetails
            title="Communications"
            Icon={Icon}
            name={FEATURES.COMMUNICATIONS}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solution
                </div>
                Real-time secure communications via voice, video, text and push-to-talk with native IOS and Android
            </div>
        </SectionDetails>
    )
}

export default memo(CommunicationsDetails)