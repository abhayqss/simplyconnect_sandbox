import React, {
    useMemo
} from 'react'

import {
    map,
    first
} from 'underscore'

import { Link } from 'react-router-dom'

import { Table } from 'components'

import {
    DateUtils as DU
} from 'lib/utils/Utils'

import { getActivityLocation } from './Utils'

import './AuditLogList.scss'

const DATE_FORMAT = DU.formats.longDateMediumTime12

function formatDate(date) {
    return DU.format(date, DATE_FORMAT)
}

export default function AuditLogList(
    {
        data,
        pagination,
        isFetching,
        onSort,
        onRefresh
    }
) {
    const columns = useMemo(() => (
        [
            {
                dataField: 'activityTitle',
                text: 'Activity',
                sort: true,
                headerAlign: 'left',
                onSort
            },
            {
                dataField: 'clients',
                text: 'Client',
                sort: true,
                onSort,
                formatter: (v, row) => (
                    <>
                        {map(row.clients, o => o.name).join(', ')}
                    </>
                )
            },
            {
                dataField: 'employeeName',
                text: 'User',
                sort: true,
                align: 'right',
                headerAlign: 'right',
                onSort
            },
            {
                dataField: 'date',
                text: 'Date',
                headerAlign: 'right',
                align: 'right',
                sort: true,
                onSort,
                formatter: v => v && formatDate(v, DATE_FORMAT)
            },
            {
                dataField: 'notes',
                text: 'Notes',
                align: 'right',
                headerAlign: 'right',
                formatter: (v, row) => (
                    <>
                        {row.notes?.join(', ')}
                    </>
                )
            },
            {
                dataField: '@action',
                text: 'Action',
                align: 'right',
                headerAlign: 'right',
                onSort,
                formatter: (v, row) => {
                    const { clients, relatedId } = row
                    const clientId = first(clients)?.id

                    const location = getActivityLocation(
                        row.activityName, { clientId, relatedId }
                    )

                    return location && (
                        <Link to={location} className="link">
                            View
                        </Link>
                    )
                }
            }
        ]
    ) , [onSort])

    return (
        <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            title="Audit Logs"
            noDataText="No audit logs."
            isLoading={isFetching}
            className='AuditLogList'
            containerClass='AuditLogListContainer'
            data={data}
            pagination={pagination}
            columns={columns}
            hasCaption={false}
            columnsMobile={['activityTitle', 'date']}
            onRefresh={onRefresh}
        />
    )
}