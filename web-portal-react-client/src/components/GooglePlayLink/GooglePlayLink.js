import React, { memo } from 'react'

import PTypes from 'prop-types'

const GooglePlayLink = ({ appId }) => {
    return (
        <a href={`https://play.google.com/store/apps/details?id=${appId}&hl=en&gl=US&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1`}>
            <img
                alt='Get it on Google Play'
                style={{ width: 285, height: 118 }}
                src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'
            />
        </a>
    )
}

GooglePlayLink.propTypes = {
    appId: PTypes.string
}

export default memo(GooglePlayLink)
