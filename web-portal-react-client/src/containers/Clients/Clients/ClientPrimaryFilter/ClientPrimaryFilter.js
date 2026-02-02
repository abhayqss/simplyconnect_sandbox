import React from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'

import { useClientPrimaryFilter } from 'hooks/business/client'

import { PrimaryFilter } from 'components'

import './ClientPrimaryFilter.scss'

export const NAME = 'CLIENT_PRIMARY_FILTER'

function mapStateToProps(state) {
    return { fields: state.client.list.dataSource.filter }
}

function ClientPrimaryFilter(
    { fields: { organizationId, communityIds }, className }
) {
    const {
        changeField,
        communities,
        organizations
    } = useClientPrimaryFilter()

    return (
        <PrimaryFilter
            communities={communities}
            organizations={organizations}
            onChangeField={changeField}
            className={cn('MarketplacePrimaryFilter', className)}
            data={{ organizationId, communityIds }}
        />
    )
}

export default connect(mapStateToProps)(ClientPrimaryFilter)