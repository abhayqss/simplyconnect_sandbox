import React, { memo } from 'react'

import cn from 'classnames'

import { compose } from 'redux'
import { connect } from 'react-redux'

import { useIncidentReportPrimaryFilter } from 'hooks/business/incident-report'

import { PrimaryFilter } from 'components'

import './IncidentReportPrimaryFilter.scss'

export const NAME = 'INCIDENT_REPORT_PRIMARY_FILTER'

function mapStateToProps(state) {
    return { fields: state.incident.report.list.dataSource.filter }
}

function IncidentReportPrimaryFilter(
    { fields: { organizationId, communityIds }, className }
) {
    const {
        changeField,
        communities,
        organizations
    } = useIncidentReportPrimaryFilter()

    return (
        <PrimaryFilter
            communities={communities}
            organizations={organizations}
            onChangeField={changeField}
            data={{ organizationId, communityIds }}
            className={cn('IncidentReportPrimaryFilter', className)}
        />
    )
}

export default compose(
    memo,
    connect(mapStateToProps)
)(IncidentReportPrimaryFilter)