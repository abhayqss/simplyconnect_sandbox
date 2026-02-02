import React, { memo } from 'react'

import { ReactComponent as Icon } from 'images/dot.svg'

import { FEATURES } from 'lib/Constants'

import SectionDetails from '../components/SectionDetails/SectionDetails'

function DocumentsDetails() {

    return (
        <SectionDetails
            title="Documents & e-Sign"
            Icon={Icon}
            name={FEATURES.DOCUMENTS}
        >
            <div className="SectionDetails-Section">
                <div className="SectionDetails-SectionTitle">
                    Solution
                </div>
                Static documents with e sign within team Care is A HIPAA-secure platform that
                supports paperless healthcare, enhances efficiency and streamlines power of attorney.
            </div>
        </SectionDetails>
    )
}

export default memo(DocumentsDetails)