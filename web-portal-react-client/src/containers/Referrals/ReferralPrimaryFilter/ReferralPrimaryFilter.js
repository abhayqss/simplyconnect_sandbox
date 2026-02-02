import React from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'

import { useReferralPrimaryFilter } from 'hooks/business/admin/referrals'

import { PrimaryFilter } from 'components'

import './ReferralPrimaryFilter.scss'

export const NAME = 'REFERRAL_PRIMARY_FILTER'

function mapStateToProps(state) {
    return { fields: state.referral.list.dataSource.filter }
}

function ReferralPrimaryFilter(
    { fields: { organizationId, communityIds }, className }
) {
    const {
        changeField,
        communities,
        organizations
    } = useReferralPrimaryFilter()

    return (
        <PrimaryFilter
            communities={communities}
            organizations={organizations}
            onChangeField={changeField}
            className={cn('ReferralPrimaryFilter', className)}
            data={{ organizationId, communityIds }}
        />
    )
}

export default connect(mapStateToProps)(ReferralPrimaryFilter)