import React, { memo } from 'react'

import { FEATURES } from 'lib/Constants'

import imageSrc from 'images/hws/consent.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { CONSENT } = FEATURES

function ConsentTile({ onClick }) {
    return (
        <SectionTile
            name={CONSENT}
            title="Consent"
            imageSrc={imageSrc}
            onClick={onClick}
        />
    )
}

export default memo(ConsentTile)