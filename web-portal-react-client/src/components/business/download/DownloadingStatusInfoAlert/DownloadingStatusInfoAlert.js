import React from 'react'

import PTypes from 'prop-types'

import { ReactComponent as Failed } from 'images/error.svg'
import { ReactComponent as Success } from 'images/finished.svg'
import { ReactComponent as Download } from 'images/download-3.svg'

import './DownloadingStatusInfoAlert.scss'

const TYPES = {
    ERROR: 'error',
    PENDING: 'pending',
    SUCCESS: 'success'
}

const ICONS_BY_TYPE = {
    [TYPES.ERROR]: Failed,
    [TYPES.PENDING]: Download,
    [TYPES.SUCCESS]: Success
}

const CONTENT_BY_TYPE = {
    [TYPES.ERROR]: 'Error',
    [TYPES.PENDING]: 'Downloading...',
    [TYPES.SUCCESS]: 'Completed'
}

function DownloadingStatusInfoAlert({
    type
}) {
    const Icon = ICONS_BY_TYPE[type]

    return (
        <>
            <Icon className="DownloadingStatusInfoAlert-Icon" />
            <span className="DownloadingStatusInfoAlert-Status">
                {CONTENT_BY_TYPE[type]}
            </span>
        </>
    )
}

DownloadingStatusInfoAlert.propTypes = {
    type: PTypes.oneOf([
        TYPES.ERROR,
        TYPES.PENDING,
        TYPES.SUCCESS
    ])
}

export default DownloadingStatusInfoAlert