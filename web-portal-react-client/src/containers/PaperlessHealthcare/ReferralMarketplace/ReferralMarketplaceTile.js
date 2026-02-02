import React, {
    memo,
    useCallback
} from 'react'

import { useHistory } from 'react-router-dom'

import {
    useCanViewMarketplaceQuery
} from 'hooks/business/Marketplace'

import { FEATURES } from 'lib/Constants'
import { path } from 'lib/utils/ContextUtils'

import imageSrc from 'images/hws/referral-marketplace.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { REFERRAL_MARKETPLACE } = FEATURES

function ReferralMarketplaceTile({ onClick }) {
    const history = useHistory()

    const {
        data: { data: canView } = {}
    } = useCanViewMarketplaceQuery()

    const _onClick = useCallback(name => {
        if (canView) {
            history.push(
                path('/marketplace'),
                { isIntroductionNeed: true }
            )
        } else onClick(name)
    }, [
        history,
        canView,
        onClick
    ])

    return (
        <SectionTile
            name={REFERRAL_MARKETPLACE}
            title="Referral Marketplace"
            imageSrc={imageSrc}
            onClick={_onClick}
        />
    )
}

export default memo(ReferralMarketplaceTile)