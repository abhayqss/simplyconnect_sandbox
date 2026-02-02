import React from 'react'

import { connect } from 'react-redux'

import { useSDoHReportPrimaryFilter } from 'hooks/business/reports'

import { PrimaryFilter } from 'components'

import './SDoHReportPrimaryFilter.scss'

function mapStateToProps(state) {
    return { fields: state.report.sdoh.list.dataSource.filter }
}

function SDoHReportPrimaryFilter(
    { fields: { organizationId, communityIds } }
) {
    const {
        changeField,
        organizations
    } = useSDoHReportPrimaryFilter()

    return (
        <PrimaryFilter
            hasCommunityField={false}

            organizations={organizations}
            data={{ organizationId, communityIds }}

            onChangeField={changeField}

            className="SDoHReportPrimaryFilter"
        />
    )
}

export default connect(mapStateToProps)(SDoHReportPrimaryFilter)