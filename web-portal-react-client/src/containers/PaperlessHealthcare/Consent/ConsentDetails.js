import React, { memo } from 'react'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function ConsentDetails() {

    return (
        <SectionDetails
            title="Consent"
            youtubeVideoId="IF_wN0U5LN4"
            name={FEATURES.CONSENT}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solutions that fit you
                </div>
                Consent management allows to seamlessly share your health information on a
                secure basis within your own personal care team.
                Consent management allows for a patient, resident or family (POA) power of
                attorney to OPTIN/OPTOUT.  So your natural supports can provide better care.
            </div>
        </SectionDetails>
    )
}

export default memo(ConsentDetails)