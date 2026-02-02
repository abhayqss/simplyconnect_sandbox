import React, { memo } from 'react'

import { FEATURES } from 'lib/Constants'

import imageSrc from 'images/hws/notify-fall-detection.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { NOTIFY_FALL_DETECTION } = FEATURES

function NotifyFallDetectionTile({ onClick }) {
    return (
        <SectionTile
            name={NOTIFY_FALL_DETECTION}
            title="NOTIFY Fall Detection"
            imageSrc={imageSrc}
            onClick={onClick}
        />
    )
}

export default memo(NotifyFallDetectionTile)