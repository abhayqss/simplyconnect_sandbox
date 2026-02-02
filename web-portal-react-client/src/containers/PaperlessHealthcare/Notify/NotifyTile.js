import React, { memo } from 'react'

import { FEATURES } from 'lib/Constants'

import imageSrc from 'images/hws/notify.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { NOTIFY } = FEATURES

function NotifyTile({ onClick }) {
    return (
        <SectionTile
            name={NOTIFY}
            title="NOTIFY"
            imageSrc={imageSrc}
            onClick={onClick}
        />
    )
}

export default memo(NotifyTile)