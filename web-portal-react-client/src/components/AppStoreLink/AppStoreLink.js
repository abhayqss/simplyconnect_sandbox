import React, { memo } from 'react'

import PTypes from 'prop-types'

const AppStoreLink = ({ appId, appName }) => {
    return (
        <a href={`https://apps.apple.com/us/app/${appName}/id${appId}?itsct=apps_box_badgeitscg=30200`}>
            <img
                alt="Download on the App Store"
                style={{ width: 250, height: 83 }}
                src="https://tools.applemediaservices.com/api/badges/download-on-the-app-store/black/en-us?size=250x83releaseDate=1631491200" 
            />
        </a>
    )
}

AppStoreLink.propTypes = {
    appId: PTypes.string,
    appName: PTypes.string
}

export default memo(AppStoreLink)
